package com.mediaAPI.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.Collections;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediaAPI.auth.AuthenticationRequest;
import com.mediaAPI.auth.AuthenticationResponse;
import com.mediaAPI.auth.RegisterRequest;
import com.mediaAPI.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationIntegrationTest {
    @LocalServerPort
    private int port;

    private static RestTemplate restTemplate;
    @Autowired
    private MediaAPIntegrationUtil mediaAPIntegrationUtil;
    private String baseUrl = "http://localhost";
    private User testUser;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void beforeSetup() {
        baseUrl = baseUrl + ":" + port;
        log.info("baseUrl: {}", baseUrl);
        testUser = mediaAPIntegrationUtil.buildUser();
    }

    @AfterEach
    public void afterSetup() {
        log.info("@AfterEach");
    }

    @Test
    @Order(1)
    void testAuthenticationControllerRegister() {
        RegisterRequest registerRequest = mediaAPIntegrationUtil.buildRegisterRequest (testUser);
        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest);
        log.info("testAuthenticationControllerRegister request: {}", request);
        ResponseEntity<AuthenticationResponse> response = restTemplate.exchange(
                baseUrl + "/auth/register",
                HttpMethod.POST,
                request,
                AuthenticationResponse.class
        );
        log.info("testAuthenticationControllerRegister response: {}", response);
        mediaAPIntegrationUtil.accessToken = response.getBody().getAccessToken();
        log.info("mediaAPIntegrationUtil.accessToken: {}", mediaAPIntegrationUtil.accessToken);
        mediaAPIntegrationUtil.refreshToken = response.getBody().getRefreshToken();
        log.info("mediaAPIntegrationUtil.refreshToken: {}", mediaAPIntegrationUtil.refreshToken);
        assertFalse(mediaAPIntegrationUtil.accessToken.isEmpty());
        assertFalse(mediaAPIntegrationUtil.refreshToken.isEmpty());
    }

    @Test
    @Order(2)
    void testAuthenticationControllerAuthenticate() {
        AuthenticationRequest authenticationRequest = mediaAPIntegrationUtil.buildAuthenticationRequest (testUser);
        HttpEntity<AuthenticationRequest> request = new HttpEntity<>(authenticationRequest);
        log.info("testAuthenticationControllerAuthenticate request: {}", request);
        ResponseEntity<AuthenticationResponse> response = restTemplate.exchange(
                baseUrl + "/auth/authenticate",
                HttpMethod.POST,
                request,
                AuthenticationResponse.class
        );
        log.info("testAuthenticationControllerAuthenticate response: {}", response);
        mediaAPIntegrationUtil.accessToken = response.getBody().getAccessToken();
        log.info("mediaAPIntegrationUtil.accessToken: {}", mediaAPIntegrationUtil.accessToken);
        mediaAPIntegrationUtil.refreshToken = response.getBody().getRefreshToken();
        log.info("mediaAPIntegrationUtil.refreshToken: {}", mediaAPIntegrationUtil.refreshToken);
        assertFalse(mediaAPIntegrationUtil.accessToken.isEmpty());
        assertFalse(mediaAPIntegrationUtil.refreshToken.isEmpty());
    }

    @Test
    @Order(3)
    void testAuthenticationControllerRefreshToken() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.refreshToken);
        log.info("testAuthenticationControllerRefreshToken headers: {}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/auth/refresh-token",
                HttpMethod.POST,
                new HttpEntity<>("", headers),
                String.class
        );
        log.info("testAuthenticationControllerRefreshToken response: {}", response);
        mediaAPIntegrationUtil.accessToken = new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
        log.info("mediaAPIntegrationUtil.accessToken: {}", mediaAPIntegrationUtil.accessToken);
        mediaAPIntegrationUtil.refreshToken =  new ObjectMapper().readTree(response.getBody()).get("refresh_token").asText();
        log.info("mediaAPIntegrationUtil.refreshToken: {}", mediaAPIntegrationUtil.refreshToken);
        assertFalse(mediaAPIntegrationUtil.accessToken.isEmpty());
        assertFalse(mediaAPIntegrationUtil.refreshToken.isEmpty());
    }
}
