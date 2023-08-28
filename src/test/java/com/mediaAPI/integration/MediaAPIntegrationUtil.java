package com.mediaAPI.integration;

import com.mediaAPI.auth.AuthenticationRequest;
import com.mediaAPI.auth.RegisterRequest;
import com.mediaAPI.model.Friendship;
import com.mediaAPI.model.Post;
import com.mediaAPI.model.RequestStatus;
import com.mediaAPI.user.User;
import com.mediaAPI.user.Role;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class MediaAPIntegrationUtil {
    String accessToken;
    String refreshToken;
    String accessTokenReceiver;
    String refreshTokenReceiver;

    private final LocalDateTime updateTime =
            LocalDateTime.of(2022, Month.AUGUST, 18, 8, 9, 2);
    User buildUser() {
        return User.builder()
                .id(1)
                .username("testUsername")
                .email("test@test.ru")
                .password("testPassword")
                .role(Role.USER)
                .build();
    }

    User buildReceiver() {
        return User.builder()
                .id(2)
                .username("testReceiver")
                .email("tesReceivert@test.ru")
                .password("testPasswordReceiver")
                .role(Role.USER)
                .build();
    }

    RegisterRequest buildRegisterRequest(User user) {
        return RegisterRequest.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

    AuthenticationRequest buildAuthenticationRequest(User user) {
        return AuthenticationRequest.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
    Post buildPost(User user) {
        return Post.builder()
                .id(1)
                .description("testDescription")
                .header("testHeader")
                .updateTime(updateTime)
                .image(new byte[]{})
                .user(user)
                .build();
    }

    Friendship buldFriendship (User sender, User receiver) {
        return Friendship.builder()
                .id(1)
                .requestSender(sender)
                .statusSender(RequestStatus.APPROVED)
                .requestReceiver(receiver)
                .statusReceiver(RequestStatus.APPROVED)
                .build();
    }
}
