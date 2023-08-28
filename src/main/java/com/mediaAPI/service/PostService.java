package com.mediaAPI.service;

import com.mediaAPI.model.Post;
import com.mediaAPI.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {

    Post create(User user, MultipartFile file, String header, String description) throws IOException;

    List<Post> readAll(Pageable page);

    Post read(int id);

    List<Post> readSubscription(User user, Pageable page);

    Post update(User user, int id, MultipartFile file, String header, String description) throws IOException;

    boolean delete(User user, int id);
}
