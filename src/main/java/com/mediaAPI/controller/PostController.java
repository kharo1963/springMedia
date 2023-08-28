package com.mediaAPI.controller;
import java.io.IOException;
import java.util.List;

import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.model.Post;
import com.mediaAPI.user.User;
import com.mediaAPI.service.PostService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final AuthenticationService authenticationService;

    @PostMapping()
    public ResponseEntity<Post> create(
            @RequestPart(required = false, name = "image") MultipartFile file,
            @RequestParam("header") String header,
            @RequestParam("description") String description
    )  throws IOException {
        User user = authenticationService.getCurrentUser();
        log.info("Получен запрос POST на добавление поста от пользователя по id {}", user.getId());
        log.info("MultipartFile file {}", file);
        return new ResponseEntity<>(postService.create(user, file, header, description),HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<Post>> readAll(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        User user = authenticationService.getCurrentUser();
        log.info("Получен запрос GET на вывод всех постов от пользователя по id {} from {} size {}", user.getId(), from, size);
        Pageable page = PageRequest.of(from, size);
        return new ResponseEntity<>(postService.readAll(page), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Post> read(@PathVariable(name = "id") int id) {
        User user = authenticationService.getCurrentUser();
        log.info("Получен запрос GET на вывод поста по id {} от пользователя по id {}", id, user.getId());
        return new ResponseEntity<>(postService.read(id), HttpStatus.OK);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<Post>> readSubscription(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        User user = authenticationService.getCurrentUser();
        log.info("Поступил запрос GET на вывод последних постов тех, на кого подписан пользователь по id {}", user.getId());
        Pageable page = PageRequest.of(from, size);
        return new ResponseEntity<>(postService.readSubscription(user, page), HttpStatus.OK);
}

    @PutMapping(value = "/{id}")
    public ResponseEntity<Post> update(
                @PathVariable(name = "id") int id,
                @RequestPart(required = false, name = "image") MultipartFile file,
                @RequestParam("header") String header,
                @RequestParam("description") String description
    )  throws IOException{
        User user = authenticationService.getCurrentUser();
        log.info("Получен запрос PUT на обновление поста по id {} от пользователя по id {}", id, user.getId());
        return new ResponseEntity<>(postService.update(user, id, file, header, description), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        User user = authenticationService.getCurrentUser();
        log.info("Получен запрос DELETE на удаление поста по id {} от пользователя по id {}", id, user.getId());
        postService.delete(user, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
