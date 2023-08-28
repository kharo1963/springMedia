package com.mediaAPI.controller;

import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.model.Friendship;
import com.mediaAPI.model.RequestStatus;
import com.mediaAPI.user.User;
import com.mediaAPI.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final AuthenticationService authenticationService;

    @PostMapping()
    public ResponseEntity<Friendship> create(@RequestParam(name = "receiver_id") int receiver_id) {
        User user = authenticationService.getCurrentUser();
        log.info("Поступил запрос POST на добавление друга от пользователя по id {} пользователю по id {}",
                user.getId(), receiver_id);
        return new ResponseEntity<>(friendshipService.create(user, RequestStatus.APPROVED, receiver_id, RequestStatus.PENDING), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<Friendship>> readAll() {
        User user = authenticationService.getCurrentUser();
        log.info("Получен запрос GET на вывод всех заявок в друзья от пользователя по id {}", user.getId());
        return new ResponseEntity<>(friendshipService.readAll(user), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Friendship> update(
            @PathVariable(name = "id") int id,
            @RequestParam("status") RequestStatus requestStatus
    ) {
        User user = authenticationService.getCurrentUser();
        log.info("Поступил запрос PUT на обновление дружбы по id {} от пользователя по id {} статус {}",
                id, user.getId(), requestStatus);
        return new ResponseEntity<>(friendshipService.update(user, id, requestStatus), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        User user = authenticationService.getCurrentUser();
        log.info("Получен запрос DELETE на удаление дружбы по id {} от пользователя по id {}", id, user.getId());
        friendshipService.delete(user, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
