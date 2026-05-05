package com.testPortal.test_management_api.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.testPortal.test_management_api.user.User;
import com.testPortal.test_management_api.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;


@Service
public class GoogleAuthService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final GoogleIdTokenVerifier verifier;

        public GoogleAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,@Value("${app.google.client-id}")String clientId){
            this.userRepository=userRepository;
            this.passwordEncoder=passwordEncoder;
            // Set up the Google Token Verifier with your Client ID
            this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singleton(clientId))
                    .build();
        }

        public User verifyGoogleTokenAndProcessUser(String idTokenString){
            try{
                // 1. verify the token with Google
                GoogleIdToken idToken = verifier.verify(idTokenString);
                if(idToken==null){
                    throw new RuntimeException("Invalid Google Id token");
                }

                // 2. Extract the user's email from the valid token
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                //3. Check if user already exits in database
                Optional<User> userOptional =userRepository.findByUsername(email);
                if(userOptional.isPresent()){
                    return userOptional.get();
                }else{
                    // 4. User doesn't exist! Let's auto-register them.
                    User newUser = new User();
                    newUser.setUsername(email);
                    // Because they use Google, they don't have a password.
                    // We generate a long, random secure password so manual login is disabled for them,
                    // but the database constraint (password cannot be null) is satisfied.

                    String randomPassword = UUID.randomUUID().toString();
                    newUser.setPassword(passwordEncoder.encode(randomPassword));

                    //Default to  Tester Role
                    newUser.setRole("ROLE_TESTER");
                    return userRepository.save(newUser);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
}
