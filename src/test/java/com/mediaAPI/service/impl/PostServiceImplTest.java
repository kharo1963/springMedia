package com.mediaAPI.service.impl;

import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.model.Post;
import com.mediaAPI.user.User;
import com.mediaAPI.repository.FriendshipRepository;
import com.mediaAPI.repository.PostRepository;
import com.mediaAPI.service.PostService;
import com.mediaAPI.user.Role;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Slf4j
class PostServiceImplTest {

    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private PostRepository postRepository;

    private PostService postService;
    AutoCloseable autoCloseable;
    Post post;
    User user;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        postService = new PostServiceImpl(postRepository, friendshipRepository, authenticationService);
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
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void create() throws IOException {
        when(postRepository.save(any(Post.class))).thenReturn(post);
        Post createdPost = postService.create(user, null, post.getHeader(),  post.getDescription());
        log.info("create:createdPost.getHeader() {}", createdPost.getHeader());
        MatcherAssert.assertThat(createdPost.getId(), is(1));
        MatcherAssert.assertThat(createdPost.getHeader(), is(post.getHeader()));
        MatcherAssert.assertThat(createdPost.getDescription(), is(post.getDescription()));
    }

    @Test
    void readAll() {
        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts);
        Pageable page = PageRequest.of(0, 20);
        when(postRepository.findAll(page))
                .thenReturn(postPage);
        List<Post> postList = postService.readAll(page);
        log.info("readAll:postList {}", postList);
        Post foundPost = postList.get(0);
        assertThat(foundPost.getId(), is(post.getId()));
        assertThat(foundPost.getHeader(), is(post.getHeader()));
        assertThat(foundPost.getDescription(), is(post.getDescription()));
        assertThat(foundPost.getUpdateTime(), is(post.getUpdateTime()));
    }

    @Test
    void read() {
        when(postRepository.findById(1)).thenReturn(Optional.ofNullable(post));
        Post readedPost = postService.read(1);
        log.info("read:readedPost {}", readedPost);
        assertThat(readedPost.getDescription(), is(post.getDescription()));
    }

    @Test
    void update()  throws IOException {
        when(postRepository.findById(1)).thenReturn(Optional.ofNullable(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        Post updatedPost = postService.update(user, post.getId(), null, post.getHeader(), post.getDescription());
        log.info("update:updatedPos {}", updatedPost);
        assertThat(updatedPost.getDescription(), is(post.getDescription()));
    }


    @Test
    void delete() {
        when(postRepository.existsById(1)).thenReturn(true);
        when(postRepository.findById(1)).thenReturn(Optional.ofNullable(post));
        boolean isDeleted = postService.delete(user,1);
        log.info("delete:isDeleted {}", isDeleted);
        assertTrue(isDeleted, "Пост удален");
    }
}