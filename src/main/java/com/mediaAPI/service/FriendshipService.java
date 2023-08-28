package com.mediaAPI.service;

import com.mediaAPI.model.Friendship;
import com.mediaAPI.model.RequestStatus;
import com.mediaAPI.user.User;

import java.util.List;

public interface FriendshipService {
    Friendship create(User user, RequestStatus statusSender, int receiver_id, RequestStatus statusReceiver);

    List<Friendship> readAll(User user);

    Friendship update(User user, int id, RequestStatus statusSender);

    boolean delete(User user, int id);

}
