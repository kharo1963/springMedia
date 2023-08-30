package com.mediaAPI.repository;

import com.mediaAPI.model.Post;
import com.mediaAPI.token.TokenRepository;
import com.mediaAPI.user.User;
import com.mediaAPI.user.Role;
import com.mediaAPI.user.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    Post post;
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .username("testUsername")
                .email("test@test.ru")
                .password("testPassword")
                .role(Role.USER)
                .build();
        user = userRepository.save(user);
        post = Post.builder()
                .id(1)
                .description("testDescription")
                .header("testHeader")
                .updateTime(LocalDateTime.of(2022, Month.AUGUST, 18, 8, 9, 2))
                .image(new byte[]{})
                .user(user)
                .build();
        postRepository.save(post);
    }

    @AfterEach
    void tearDown() {
        post = null;
        postRepository.deleteAll();
    }

    @Test
    void testFindById_Found()
    {
        Post post = postRepository.findById(1).get();
        assertThat(post.getId()).isEqualTo(this.post.getId());
        assertThat(post.getDescription()).isEqualTo(post.getDescription());
    }

    // Test case FAILURE
    @Test
    void testFindById_NotFound()
    {
        assertThat(postRepository.findById(1).isEmpty()).isTrue();
    }
}
