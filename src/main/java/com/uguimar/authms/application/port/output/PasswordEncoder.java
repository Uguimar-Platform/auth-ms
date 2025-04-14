package com.uguimar.authms.application.port.output;

public interface PasswordEncoder {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
