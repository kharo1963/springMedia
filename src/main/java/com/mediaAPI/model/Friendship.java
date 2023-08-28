package com.mediaAPI.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mediaAPI.user.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "friendship")
public class Friendship {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_sender_id")
    @ToString.Exclude
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("request_sender_id")
    private User requestSender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_sender")
    private RequestStatus statusSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_receiver_id")
    @ToString.Exclude
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("request_receiver_id")
    private User requestReceiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_receiver")
    private RequestStatus statusReceiver;
}
