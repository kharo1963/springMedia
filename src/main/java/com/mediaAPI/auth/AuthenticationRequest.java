package com.mediaAPI.auth;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

  private String username;
  @ToString.Exclude
  String password;
}
