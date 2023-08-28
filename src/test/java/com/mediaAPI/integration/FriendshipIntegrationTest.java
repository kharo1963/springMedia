package com.mediaAPI.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.util.Collections;

import com.mediaAPI.auth.AuthenticationResponse;
import com.mediaAPI.auth.RegisterRequest;
import com.mediaAPI.model.Friendship;
import com.mediaAPI.model.RequestStatus;
import com.mediaAPI.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.mediaAPI.model.Post;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FriendshipIntegrationTest {
    @LocalServerPort
    private int port;

    private static RestTemplate restTemplate;
    @Autowired
    private MediaAPIntegrationUtil mediaAPIntegrationUtil;
    private String baseUrl = "http://localhost";
    private User testUser;
    private User testReceiver;
    Friendship testFriendship;
    private Post testPost;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void beforeSetup() {
        baseUrl = baseUrl + ":" + port;
        log.info("baseUrl: {}", baseUrl);
        testUser = mediaAPIntegrationUtil.buildUser();
        testReceiver = mediaAPIntegrationUtil.buildReceiver();
        testPost = mediaAPIntegrationUtil.buildPost (testUser);
        testFriendship =  mediaAPIntegrationUtil.buldFriendship (testUser, testReceiver);
    }

    @AfterEach
    public void afterSetup() {
        log.info("@AfterEach");
        //postRepository.deleteAll();
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
    }

    @Test
    @Order(2)
    void testAuthenticationControllerRegisterReceiver() {
        RegisterRequest registerRequest = mediaAPIntegrationUtil.buildRegisterRequest (testReceiver);
        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest);
        log.info("testAuthenticationControllerRegisterReceiver request: {}", request);
        ResponseEntity<AuthenticationResponse> response = restTemplate.exchange(
                baseUrl + "/auth/register",
                HttpMethod.POST,
                request,
                AuthenticationResponse.class
        );
        log.info("testAuthenticationControllerRegister response: {}", response);
        mediaAPIntegrationUtil.accessTokenReceiver = response.getBody().getAccessToken();
        log.info("mediaAPIntegrationUtilReceiver.accessTokenReceiver: {}", mediaAPIntegrationUtil.accessTokenReceiver);
        mediaAPIntegrationUtil.refreshTokenReceiver = response.getBody().getAccessToken();
        log.info("mediaAPIntegrationUtilReceiver.refreshTokenReceiver: {}", mediaAPIntegrationUtil.refreshTokenReceiver);
    }

    @Test
    @Order(3)
    void testFriendshipControllerСreate() throws IOException {
        String url = baseUrl + "/friendships";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("receiver_id", testReceiver.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        log.info("testFriendshipControllerСreate headers: {}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.POST,
                new HttpEntity<>("", headers),
                String.class
        );
        log.info("testFriendshipControllerСreate response: {}", response);
    }

    @Test
    @Order(4)
    void testFriendshipControllerUpdate() throws IOException {
        String url = baseUrl + "/friendships/" + testFriendship.getId();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("receiver_id", testReceiver.getId())
                .queryParam("status", RequestStatus.APPROVED);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessTokenReceiver);
        log.info("testFriendshipControllerUpdate headers: {}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>("", headers),
                String.class
        );
        log.info("testFriendshipControllerUpdate response: {}", response);
    }

    @Test
    @Order(5)
    void testFriendshipControllerReadAll() throws IOException {
        String url = baseUrl + "/friendships";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        log.info("testFriendshipControllerReadAll headers: {}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>("", headers),
                String.class
        );
        log.info("testFriendshipControllerReadAll response: {}", response);
    }

    @Test
    @Order(6)
    void testFriendshipControllerDelete() throws IOException {
        String url = baseUrl + "/friendships/" + testFriendship.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        log.info("testFriendshipControllerDelete headers: {}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>("", headers),
                String.class
        );
        log.info("testFriendshipControllerDelete response: {}", response);
    }
}
