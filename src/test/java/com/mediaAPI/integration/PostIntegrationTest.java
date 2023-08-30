package com.mediaAPI.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.mediaAPI.model.Post;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostIntegrationTest {
    @LocalServerPort
    private int port;

    private static RestTemplate restTemplate;
    @Autowired
    private MediaAPIntegrationUtil mediaAPIntegrationUtil;
    private String baseUrl = "http://localhost";
    private User testUser;
    private Post testPost;
    private User testReceiver;
    Friendship testFriendship;

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
        mediaAPIntegrationUtil.refreshTokenReceiver = response.getBody().getRefreshToken();
        log.info("mediaAPIntegrationUtilReceiver.refreshTokenReceiver: {}", mediaAPIntegrationUtil.refreshTokenReceiver);
        assertFalse(mediaAPIntegrationUtil.accessTokenReceiver.isEmpty());
        assertFalse(mediaAPIntegrationUtil.refreshTokenReceiver.isEmpty());
    }

    @Test
    @Order(3)
    void testFriendshipControllerCreate() {
        String url = baseUrl + "/friendships";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("receiver_id", testReceiver.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        log.info("testFriendshipControllerСreate headers: {}", headers);
        ResponseEntity<Friendship> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.POST,
                new HttpEntity<>("", headers),
                Friendship.class
        );
        log.info("testFriendshipControllerСreate response: {}", response);
        assertNotNull(response.getBody());
    }

    @Test
    @Order(4)
    void testFriendshipControllerUpdate() {
        String url = baseUrl + "/friendships/" + testFriendship.getId();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("receiver_id", testReceiver.getId())
                .queryParam("status", RequestStatus.APPROVED);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessTokenReceiver);
        log.info("testFriendshipControllerUpdate headers: {}", headers);
        ResponseEntity<Friendship> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.PUT,
                new HttpEntity<>("", headers),
                Friendship.class
        );
        log.info("testFriendshipControllerUpdate response: {}", response);
        assertNotNull(response.getBody());
    }

    @Test
    @Order(5)
    void testPostControllerCreate() {
        String url = baseUrl + "/posts";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("header", testPost.getHeader())
                .queryParam("description", testPost.getDescription());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        log.info("testPostControllerСreate headers: {}", headers);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Path path = Paths.get("m-ext-gcd.txt");
        File file = path.toFile();
        body.add("image", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        log.info("testPostControllerСreate httpEntity: {}", httpEntity);
        ResponseEntity<Post> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.POST,
                httpEntity,
                Post.class
        );
        log.info("testPostControllerСreate response: {}", response);
        assertNotNull(response.getBody());
    }

    @Test
    @Order(6)
    void testPostControllerCreateReceiver() {
        String url = baseUrl + "/posts";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("header", testPost.getHeader())
                .queryParam("description", testPost.getDescription());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessTokenReceiver);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        log.info("testPostControllerСreateReceiver headers: {}", headers);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Path path = Paths.get("m-ext-gcd.txt");
        File file = path.toFile();
        body.add("image", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        log.info("testPostControllerСreateReceiver httpEntity: {}", httpEntity);
        ResponseEntity<Post> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.POST,
                httpEntity,
                Post.class
        );
        log.info("testPostControllerСreate response: {}", response);
        assertNotNull(response.getBody());
    }

    @Test
    @Order(7)
    void testPostControllerReadAll()  {
        String url = baseUrl + "/posts";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("from", 0)
                .queryParam("size", 20);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        log.info("testPostControllerReadAll headers: {}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>("", headers),
                String.class
        );
        log.info("testPostControllerReadAll response: {}", response);
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @Order(8)
    void testPostControllerRead() {
        String url = baseUrl + "/posts/" + testPost.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        log.info("testPostControllerRead headers: {}", headers);
        ResponseEntity<Post> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>("", headers),
                Post.class
        );
        log.info("testPostControllerRead response: {}", response);
        assertNotNull(response.getBody());
    }

    @Test
    @Order(9)
    void testPostControllerReadSubscription() {
        String url = baseUrl + "/posts/subscriptions";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("from", 0)
                .queryParam("size", 20);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        log.info("testPostControllerReadAll headers: {}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>("", headers),
                String.class
        );
        log.info("testPostControllerReadAll response: {}", response);
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @Order(10)
    void testPostControllerUpdate() {
        String url = baseUrl + "/posts/" + testPost.getId();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("header", testPost.getHeader() + "u")
                .queryParam("description", testPost.getDescription() + "u");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        log.info("testPostControllerUpdate headers: {}", headers);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Path path = Paths.get("m-spin-cube.txt");
        File file = path.toFile();
        body.add("image", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        log.info("testPostControllerUpdate httpEntity: {}", httpEntity);
        ResponseEntity<Post> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.PUT,
                httpEntity,
                Post.class
        );
        log.info("testPostControllerUpdate response: {}", response);
        assertNotNull(response.getBody());
    }

    @Test
    @Order(11)
    void testPostControllerDelete() {
        String url = baseUrl + "/posts/" + testPost.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ mediaAPIntegrationUtil.accessToken);
        log.info("testPostControllerDelete headers: {}", headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>("", headers),
                String.class
        );
        log.info("testPostControllerDelete response: {}", response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}

