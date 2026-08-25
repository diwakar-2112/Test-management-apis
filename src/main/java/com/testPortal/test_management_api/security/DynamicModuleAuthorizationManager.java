package com.testPortal.test_management_api.security;

import com.testPortal.test_management_api.administration.Role;
import com.testPortal.test_management_api.administration.RoleModuleAccess;
import com.testPortal.test_management_api.user.User;
import com.testPortal.test_management_api.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Supplier;

@Component
public class DynamicModuleAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final UserRepository userRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public DynamicModuleAuthorizationManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication auth = authenticationSupplier.get();

        // 1. Must be authenticated
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return new AuthorizationDecision(false);
        }

        // 2. SUPER_ADMIN gets a master key to everything
        boolean isSuperAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (isSuperAdmin) {
            return new AuthorizationDecision(true);
        }

        // 3. Extract request details
        HttpServletRequest request = context.getRequest();
        String requestPath = request.getRequestURI(); // e.g. "/api/projects"
        String method = request.getMethod().toUpperCase(); // "GET", "POST", "PUT", "DELETE"

        // 4. Fetch the user and role with permissions
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null || user.getRole() == null || user.getRole().getModuleAccessList() == null) {
            return new AuthorizationDecision(false);
        }

        Role role = user.getRole();
        List<RoleModuleAccess> accessList = role.getModuleAccessList();

        // 5. Match request path against all modules assigned to this role
        for (RoleModuleAccess access : accessList) {
            String rawUrl = access.getModule().getModuleUrl();
            if (rawUrl == null || rawUrl.isBlank()) {
                continue;
            }

            // Normalize url: e.g. "/projects" or "projects" -> "/projects"
            String cleanUrl = rawUrl.startsWith("/") ? rawUrl : "/" + rawUrl;
            // Support both with and without hyphen (e.g. /test-suites and /testsuites)
            String cleanUrlWithoutHyphens = cleanUrl.replace("-", "");

            // Build matching patterns for API endpoints
            boolean matches = matchPath(requestPath, cleanUrl) || matchPath(requestPath, cleanUrlWithoutHyphens);

            if (matches) {
                boolean hasPermission = switch (method) {
                    case "POST" -> access.isCanCreate();
                    case "PUT", "PATCH" -> access.isCanEdit();
                    case "DELETE" -> access.isCanDelete();
                    case "GET" -> {
                        // For GET: check if it's viewing or listing
                        // If either canView or canList is true, allow GET
                        yield access.isCanList() || access.isCanView();
                    }
                    default -> false;
                };

                // If user has permission in ANY matching module row, grant access
                if (hasPermission) {
                    return new AuthorizationDecision(true);
                }
            }
        }

        // If no matching module gave permission, deny
        return new AuthorizationDecision(false);
    }

    private boolean matchPath(String requestPath, String moduleBase) {
        // e.g. moduleBase = "/projects"
        // matches: /api/projects, /api/projects/**, /api/admin/projects/**
        String[] patterns = {
                "/api" + moduleBase,
                "/api" + moduleBase + "/**",
                "/api/admin" + moduleBase,
                "/api/admin" + moduleBase + "/**",
                "/api/**" + moduleBase + "/**" // for nested like /api/projects/1/testsuites
        };

        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }
}