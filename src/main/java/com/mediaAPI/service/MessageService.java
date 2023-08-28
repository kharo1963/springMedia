package com.mediaAPI.service;

import com.mediaAPI.model.Message;
import com.mediaAPI.user.User;

import java.util.List;

public interface MessageService {
    List<Message> readAllById(User currentUser, Integer friend_id);

    Message create(User user, Integer message_receiver_id, String content);

}
