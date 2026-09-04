package com.testPortal.test_management_api.common;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/lookups")
public class LookupController {
    private final List<LookupProvider> providers;

    public LookupController(List<LookupProvider> providers){
        this.providers=providers;
    }

    @GetMapping
    public Map<String,List<LookupItem>> getLookups(@RequestParam List<String> types){
        Map<String,List<LookupItem>> response =  new HashMap<>();

        for(String type:types){
            for(LookupProvider provider:providers){
                if(provider.getType().equalsIgnoreCase(type)){
                    response.put(type,provider.getDropdownItems());
                }
            }
        }
        return response;
    }
}
