package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.dto.LoginDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AuthService {

    private final String keycloakUrl = "https://34.66.228.78:8443";
    private final String realm = "testfnf";
    private final String clientId = "React-app";
    private final String clientSecret = "bITinYIxVGia4WimDfawoADptElOjA6K";

    public ResponseEntity<?> authenticate(LoginDto loginRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("username", loginRequest.getUsername());
        params.add("password", loginRequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        String tokenEndpoint = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> tokenData = response.getBody();
                String accessToken = (String) tokenData.get("access_token");

                // Decode JWT token
                DecodedJWT decodedJWT = JWT.decode(accessToken);

                // Extract realm roles
                List<String> roles = decodedJWT
                        .getClaim("realm_access")
                        .asMap() != null
                        ? (List<String>) ((Map<String, Object>) decodedJWT.getClaim("realm_access").asMap()).get("roles")
                        : new ArrayList<>();

                // Pick the first role that matches your app roles
                String role = roles.stream()
                        .filter(r -> r.equalsIgnoreCase("Host") || r.equalsIgnoreCase("Client"))
                        .findFirst()
                        .orElse("Unknown");

                // Optional: extract more info
                String username = decodedJWT.getClaim("preferred_username").asString();
                String email = decodedJWT.getClaim("email").asString();

                Map<String, Object> result = new HashMap<>();
                result.put("token", accessToken);
                result.put("role", role);
                result.put("username", username);
                result.put("email", email);

                return ResponseEntity.ok(result);
            }

            return ResponseEntity.status(response.getStatusCode()).body("Failed to authenticate");

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
