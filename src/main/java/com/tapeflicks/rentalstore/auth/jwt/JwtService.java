package com.tapeflicks.rentalstore.auth.jwt;

import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("java:S1135")
public class JwtService {

  public Long extractUserId(String token) throws InvalidOneTimeTokenException {
    return fakeExtractUserId(token);
  }

  /**
   * PLACEHOLDER for user identity extraction from an authentication token.
   *
   * <p>Authentication is not yet implemented. This method temporarily treats the raw token value as
   * a user ID to allow end-to-end testing of the rental flow without a working auth layer.
   *
   * <p><b>TODO:</b> Replace with proper JWT validation and principal extraction once authentication
   * is implemented.
   *
   * @param token the raw token value from the request header (currently expected to be a numeric
   *     user ID)
   * @return the user ID parsed directly from the token string
   */
  private Long fakeExtractUserId(String token) {
    return Long.valueOf(token);
  }
}
