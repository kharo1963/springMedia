package com.mediaAPI.model;

import com.fasterxml.jackson.annotation.*;
import com.mediaAPI.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "header")
    private String header;

    @Column(name = "description")
    private String description;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User user;

}
