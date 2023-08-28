package com.mediaAPI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.model.Post;
import com.mediaAPI.user.User;
import com.mediaAPI.service.PostService;
import com.mediaAPI.config.JwtService;
import com.mediaAPI.token.TokenRepository;
import com.mediaAPI.user.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PostController.class)
class PostControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private PostService postService;
    @MockBean
    private JwtService JwtService;
    @MockBean
    TokenRepository tokenRepository;
    @MockBean
    AuthenticationService service;

    Post postOne;
    Post postTwo;
    User user;
    List<Post> postList= new ArrayList<>();
    private Pageable page;
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        user = User.builder()
                .id(1)
                .username("testUsername")
                .email("test@test.ru")
                .password("testPassword")
                .role(Role.USER)
                .build();
        postOne = new Post(1, "Header1","Amazon", null, Base64.getDecoder().decode("gsttgggshssj"), user);
        postTwo = new Post(2, "Header2","Amazon", null, Base64.getDecoder().decode("uiwiwwi"), user);
        postList.add(postOne);
        postList.add(postTwo);
        page = PageRequest.of(0, 20);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void create() {
    }

    @Test
    void readAll() throws Exception {
        when(postService.readAll(page)).thenReturn(postList);
        this.mvc.perform(get("/posts"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void read() {
    }

    @Test
    void update() throws Exception {
        when(postService.update(user, postOne.getId(), null, postOne.getHeader(), postOne.getDescription())).thenReturn(postOne);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(postOne);
        mvc.perform(put("/posts/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void delete() {
    }
}