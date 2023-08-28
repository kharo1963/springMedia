package com.mediaAPI.repository;

import com.mediaAPI.model.Post;
import com.mediaAPI.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUserInOrderByUpdateTimeDesc(List<User> users, Pageable page);
}
