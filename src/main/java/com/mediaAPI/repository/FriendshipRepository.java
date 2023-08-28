package com.mediaAPI.repository;

import com.mediaAPI.model.Friendship;
import com.mediaAPI.model.RequestStatus;
import com.mediaAPI.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository  extends JpaRepository<Friendship, Integer> {

    boolean existsByRequestSenderAndStatusSenderAndRequestReceiverAndStatusReceiver(User currentUser, RequestStatus requestStatus, User requestReceiver, RequestStatus requestStatus1);

    List<Friendship> findAllByRequestSenderOrRequestReceiver(User user, User user1);

    List<Friendship> findAllByRequestSenderAndStatusSenderOrRequestReceiverAndAndStatusReceiver(User user, RequestStatus requestStatus, User user1, RequestStatus requestStatus1);
}
