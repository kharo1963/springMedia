package com.mediaAPI.service.impl;

import com.mediaAPI.exception.DataNotFoundException;
import com.mediaAPI.model.Message;
import com.mediaAPI.model.RequestStatus;
import com.mediaAPI.user.User;
import com.mediaAPI.repository.FriendshipRepository;
import com.mediaAPI.repository.MessageRepository;
import com.mediaAPI.service.MessageService;
import com.mediaAPI.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Override
    public List<Message> readAllById(User currentUser, Integer friend_id) {

        User requestReceiver = checkUserId(friend_id);
        if (!friendshipRepository.existsByRequestSenderAndStatusSenderAndRequestReceiverAndStatusReceiver
                (currentUser, RequestStatus.APPROVED, requestReceiver, RequestStatus.APPROVED) &&
           !friendshipRepository.existsByRequestSenderAndStatusSenderAndRequestReceiverAndStatusReceiver
                (requestReceiver, RequestStatus.APPROVED, currentUser, RequestStatus.APPROVED)
        ) {
            throw new DataNotFoundException("Дружба между пользователем по id " + currentUser.getId() + " и " + friend_id
                    + " не найдена в базе данных");
        }
        return messageRepository.findAllByMessageSenderAndMessageReceiverOrMessageReceiverAndMessageSender(
                currentUser, requestReceiver, requestReceiver, currentUser
        );
    }

    @Override
    public Message create(User user, Integer message_receiver_id, String content) {
        Message message = new Message();
        message.setMessageSender(user);
        message.setMessageReceiver(checkUserId(message_receiver_id));
        message.setContent(content);
        message.setUpdateTime(LocalDateTime.now());
        return messageRepository.save(message);
    }

    private User checkUserId(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Пользователь по id " +
                userId + " не найден в базе данных"));
    }
}

