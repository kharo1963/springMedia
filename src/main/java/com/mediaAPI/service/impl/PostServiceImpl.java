package com.mediaAPI.service.impl;

import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.exception.DataNotFoundException;
import com.mediaAPI.model.Friendship;
import com.mediaAPI.model.Post;
import com.mediaAPI.model.RequestStatus;
import com.mediaAPI.user.User;
import com.mediaAPI.repository.FriendshipRepository;
import com.mediaAPI.repository.PostRepository;
import com.mediaAPI.service.PostService;
import com.mediaAPI.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final FriendshipRepository friendshipRepository;
    private final AuthenticationService authenticationService;

    @Override
    public Post create(User user, MultipartFile file, String header, String description) throws IOException {
        Post post = new Post();
        post.setUser(user);
        post.setHeader(header);
        post.setDescription(description);
        if (file != null) {
            post.setImage(file.getBytes());
        }
        post.setUpdateTime(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Override
    public List<Post> readAll(Pageable page) {
        return postRepository.findAll(page).stream()
                .collect(Collectors.toList());
    }

    @Override
    public Post read(int id) {
        if(postRepository.findById(id).isEmpty())
            throw new DataNotFoundException("Не найден пост с id = " + id);
        return postRepository.findById(id).get();
    }

    @Override
    public List<Post> readSubscription(User user, Pageable page){
        List<Friendship> friendships = friendshipRepository
                .findAllByRequestSenderAndStatusSenderOrRequestReceiverAndAndStatusReceiver
                        (user, RequestStatus.APPROVED, user, RequestStatus.APPROVED);
        log.info("readSubscription() List<Friendship> friendships {}",friendships);
        List<Integer> publishersIds = friendships.stream().map(
                friendship -> user.getId() == friendship.getRequestSender().getId()
                        ? friendship.getRequestReceiver().getId()
                        : friendship.getRequestSender().getId()
                )
                .toList();
        log.info("readSubscription() List<Integer> publishersIds {}", publishersIds);
        UserRepository userRepository = authenticationService.getUserRepository();
        List<User> users = userRepository.findAllByIdIn(publishersIds);
        return postRepository.findByUserInOrderByUpdateTimeDesc(users, page);
    }

    @Override
    public Post update(User user, int id, MultipartFile file, String header, String description) throws IOException{
        Optional<Post> optionalPost = postRepository.findById(id);
        if (!optionalPost.isEmpty()) {
            Post post = optionalPost.get();
            checkIfUserOwner(post, user);
            post.setHeader(header);
            post.setDescription(description);
            if (file != null) {
                post.setImage(file.getBytes());
            }
            post.setUpdateTime(LocalDateTime.now());
            return postRepository.save(post);
        }
        throw new DataNotFoundException("Не найден пост с id = " + id);
    }

    @Override
    public boolean delete(User user, int id) {
        if (postRepository.existsById(id)) {
            Post post = checkPostId(id);
            checkIfUserOwner(post, user);
            postRepository.deleteById(id);
            return true;
        }
        throw new DataNotFoundException("Не найден пост с id = " + id);
    }

    private Post checkPostId(int postId) {
        return postRepository.findById(postId).orElseThrow(() -> new DataNotFoundException("Пост по id " +
                postId + " не найден в базе данных"));
    }
    private void checkIfUserOwner(Post post, User user) {
        if (!Objects.equals(post.getUser().getId(), user.getId())) {
            throw new DataNotFoundException("Пользователь по id " +
                    user.getId() + " не делал пост по id " + post.getId());
        }
    }

}
