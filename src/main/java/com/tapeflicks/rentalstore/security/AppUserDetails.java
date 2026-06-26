package com.tapeflicks.rentalstore.security;

import com.tapeflicks.rentalstore.user.User;
import java.util.Collection;
import java.util.Collections;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This class wraps real user data so the rest of the security machinery so JWT
 * filter, @AuthenticationPrincipal, @PreAuthorize can work with it.
 *
 * <p>PLACEHOLDER: id/email are hardcoded for now. Once real auth exists, an instance of this class
 * gets constructed from the authenticated User entity (typically inside the JWT filter, after the
 * token is validated and the user is loaded from the DB).
 */
@RequiredArgsConstructor
@Getter
@Builder
@SuppressWarnings({"serial", "java:S4144"})
public class AppUserDetails implements UserDetails {

  private final Long id;
  private final String email;
  private final String password; // hashed password, never plaintext
  private final Role role;

  private static final boolean FAKE_CHECK_PLACEHOLDER = true;

  public static AppUserDetails fromUser(User user) {
    return new AppUserDetails(
        user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return FAKE_CHECK_PLACEHOLDER;
  }

  @Override
  public boolean isAccountNonLocked() {
    return FAKE_CHECK_PLACEHOLDER;
  } // NOSONAR

  @Override
  public boolean isCredentialsNonExpired() {
    return FAKE_CHECK_PLACEHOLDER;
  } // NOSONAR

  @Override
  public boolean isEnabled() {
    return FAKE_CHECK_PLACEHOLDER;
  } // NOSONAR
}
