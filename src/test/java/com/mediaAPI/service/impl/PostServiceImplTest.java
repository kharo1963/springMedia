package com.mediaAPI.service.impl;

import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.model.Post;
import com.mediaAPI.user.User;
import com.mediaAPI.repository.FriendshipRepository;
import com.mediaAPI.repository.PostRepository;
import com.mediaAPI.service.PostService;
import com.mediaAPI.user.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private AuthenticationService authenticationService;
    private PostService postService;
    AutoCloseable autoCloseable;
    Post post;
    User user;
    private Pageable page;

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
        post = new Post(1,"Header1","Amazon", null, Base64.getDecoder().decode("gsttgggshssj"), user);
        page = PageRequest.of(0, 20);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void create() throws IOException {
        mock(Post.class);
        mock(PostRepository.class);

        when(postRepository.save(post)).thenReturn(post);
        assertThat(postService.create(user, null, post.getHeader(), post.getDescription())).isEqualTo(true);
    }

    @Test
    void readAll() {
        mock(Post.class);
        mock(PostRepository.class);

        when(postRepository.findAll()).thenReturn(new ArrayList<Post>(
                Collections.singleton(post)
        ));

        assertThat(postService.readAll(page).get(0).getDescription()).
                isEqualTo(post.getDescription());
    }

    @Test
    void read() {
        mock(Post.class);
        mock(PostRepository.class);

        when(postRepository.findById(1)).thenReturn(Optional.ofNullable(post));
        assertThat(postService.read(1).getDescription())
                .isEqualTo(post.getDescription());
    }

    @Test
    void update()  throws IOException {
        mock(Post.class);
        mock(PostRepository.class);

        when(postRepository.existsById(1)).thenReturn(true);
        when(postRepository.save(post)).thenReturn(post);
        assertThat(postService.update(user, post.getId(), null, post.getHeader(), post.getDescription())).isEqualTo(true);
    }

    @Test
    void delete() {
        mock(Post.class);
        mock(PostRepository.class);

        when(postRepository.existsById(1)).thenReturn(true);
        assertThat(postService.delete(user,1)).isEqualTo(true);
    }
}