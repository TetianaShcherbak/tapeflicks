package com.tapeflicks.rentalstore.user;

import com.tapeflicks.rentalstore.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository userRepository;

  public User getUser(Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
  }
}
