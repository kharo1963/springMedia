package com.mediaAPI.auth;

import com.mediaAPI.user.Role;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String username;
  private String email;
  @ToString.Exclude
  private String password;
  private Role role;
}
