package com.mediaAPI.controller;

import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.model.Message;
import com.mediaAPI.user.User;
import com.mediaAPI.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final AuthenticationService authenticationService;

    @PostMapping()
    public ResponseEntity<Message> create (
            @RequestParam(name = "receiver_id") int receiver_id,
            @RequestParam (name = "content") String content
    ) {
        User user = authenticationService.getCurrentUser();
        log.info("Поступил запрос POST на добавление сообщения от пользователя по id {} пользователю по id {}",
                user.getId(), receiver_id);
        return new ResponseEntity<>(messageService.create(user, receiver_id, content), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public List<Message> readAllById(@PathVariable(name = "id") int id) {
        User user = authenticationService.getCurrentUser();
        log.info("Получен запрос GET на всех сообщений пользователя по id {} от пользователя по id {}", id, user.getId());
        return messageService.readAllById(user, id);
    }
}

