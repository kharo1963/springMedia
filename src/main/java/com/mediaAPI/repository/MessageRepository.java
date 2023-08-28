package com.mediaAPI.repository;

import com.mediaAPI.model.Message;
import com.mediaAPI.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByMessageSenderAndMessageReceiverOrMessageReceiverAndMessageSender(User currentUser, User requestReceiver, User requestReceiver1, User currentUser1);
}
