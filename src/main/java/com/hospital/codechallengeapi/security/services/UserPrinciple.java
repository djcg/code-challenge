package com.hospital.codechallengeapi.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hospital.codechallengeapi.entity.HospitalUserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class UserPrinciple implements UserDetails {
  private static final long serialVersionUID = 1L;

  private final UUID id;

  private final String name;

  private final String username;

  @JsonIgnore
  private final String password;

  private final Collection<? extends GrantedAuthority> authorities;

  public static UserPrinciple build(HospitalUserEntity user) {
    List<GrantedAuthority> authorities =
        user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .collect(Collectors.toList());

    return new UserPrinciple(
        user.getId(), user.getName(), user.getUsername(), user.getPassword(), authorities);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
