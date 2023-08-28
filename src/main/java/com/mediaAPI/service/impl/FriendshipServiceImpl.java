package com.mediaAPI.service.impl;

import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.exception.DataNotFoundException;
import com.mediaAPI.model.Friendship;
import com.mediaAPI.model.RequestStatus;
import com.mediaAPI.user.User;
import com.mediaAPI.repository.FriendshipRepository;
import com.mediaAPI.service.FriendshipService;
import com.mediaAPI.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Override
    public Friendship create(User user, RequestStatus statusSender, int receiver_id, RequestStatus statusReceiver) {
        Friendship friendship = new Friendship();
        friendship.setRequestSender(user);
        friendship.setStatusSender(statusSender);
        friendship.setRequestReceiver(checkUserId(receiver_id));
        friendship.setStatusReceiver (statusReceiver);
        return friendshipRepository.save(friendship);
    }

    @Override
    public List<Friendship> readAll(User user) {
        return friendshipRepository.findAllByRequestSenderOrRequestReceiver(user, user);
    }

    @Override
    public Friendship update(User user, int id, RequestStatus requestStatus) {
        Optional<Friendship> optionalFriendship = friendshipRepository.findById(id);
        if (!optionalFriendship.isEmpty()) {
            Friendship friendship = optionalFriendship.get();
            if (Objects.equals(friendship.getRequestSender().getId(), user.getId())) {
                friendship.setStatusSender(requestStatus);
            }
            else if (Objects.equals(friendship.getRequestReceiver().getId(), user.getId())) {
                friendship.setStatusReceiver(requestStatus);
            }
            else
                throw new DataNotFoundException("Пользователь по id " +
                        user.getId() + " не присутствует в заявке в друзья по id " + friendship.getId());
            friendship.setStatusSender(requestStatus);
            return friendshipRepository.save(friendship);
        }
        throw new DataNotFoundException("Не найдена дружба с id = " + id);
    }

    @Override
    public boolean delete(User user, int id) {
        if (friendshipRepository.existsById(id)) {
            Friendship friendship = checkFriendshipId(id);
            checkIfUserOwner(friendship, user);
            friendshipRepository.deleteById(id);
            return true;
        }
        throw new DataNotFoundException("Не ннайдена дружба с id = " + id);
    }

    private User checkUserId(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Пользователь по id " +
                userId + " не найден в базе данных"));
    }
    private Friendship checkFriendshipId(int friendshipId) {
        return friendshipRepository.findById(friendshipId).orElseThrow(() -> new DataNotFoundException("Дружба по id " +
                friendshipId + " не найдена в базе данных"));
    }

    private void checkIfUserOwner(Friendship friendship, User user) {
        if (!Objects.equals(friendship.getRequestSender().getId(), user.getId())) {
            throw new DataNotFoundException("Пользователь по id " +
                    user.getId() + " не делал заявку в друзья по id " + friendship.getId());
        }
    }
}
