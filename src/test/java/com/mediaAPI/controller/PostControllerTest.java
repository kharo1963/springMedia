package com.mediaAPI.controller;

import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.model.Post;
import com.mediaAPI.user.User;
import com.mediaAPI.service.PostService;
import com.mediaAPI.config.JwtService;
import com.mediaAPI.token.TokenRepository;
import com.mediaAPI.user.Role;
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

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    AuthenticationService authenticationService;

    Post post;
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
        post = Post.builder()
                .id(1)
                .header("Header1")
                .description("Amazon")
                .updateTime(LocalDateTime.of(2022, Month.AUGUST, 18, 8, 9, 2))
                .image(null)
                .user(user)
                .build();
        postList.add(post);
        page = PageRequest.of(0, 20);
    }

    @Test
    void testCreate() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(postService.create(user, null, post.getHeader(), post.getDescription())).thenReturn(post);
        mvc.perform(post("/posts")
                        .param("header", post.getHeader())
                        .param("description", post.getDescription())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    void testReadAll() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(postService.readAll(page)).thenReturn(postList);
        this.mvc.perform(get("/posts"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testRead()  throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(postService.read(1)).thenReturn(post);
        mvc.perform(get("/posts/1"))
                .andExpect(status().isOk());
    }

    @Test
    void teatUpdate() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(postService.update(user, post.getId(), null, post.getHeader(), post.getDescription())).thenReturn(post);
        mvc.perform(put("/posts/" + 1)
                .param("header", post.getHeader())
                .param("description", post.getDescription())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(postService.delete(user, 1)).thenReturn(true);
        mvc.perform(delete("/posts/1"))
                .andExpect(status().isOk());
    }
}