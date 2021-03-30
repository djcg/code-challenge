package com.hospital.codechallengeapi.security.services;

import com.hospital.codechallengeapi.entity.HospitalUserEntity;
import com.hospital.codechallengeapi.repository.HospitalUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

  private HospitalUserRepository hospitalUserRepository;

  @Autowired
  public UserDetailsServiceImpl(HospitalUserRepository hospitalUserRepository) {
    this.hospitalUserRepository = hospitalUserRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    HospitalUserEntity user =
        hospitalUserRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User Not Found with -> username or email : " + username));

    return UserPrinciple.build(user);
  }
}
