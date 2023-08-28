package com.mediaAPI.repository;

import com.mediaAPI.model.Post;
import com.mediaAPI.user.User;
import com.mediaAPI.user.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;
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
        post = new Post(1, "Header1", "Amazon", null, Base64.getDecoder().decode("gsttgggshssj"), user);
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
