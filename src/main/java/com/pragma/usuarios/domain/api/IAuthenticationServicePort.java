package com.pragma.usuarios.domain.api;

public interface IAuthenticationServicePort {

    void authenticate(String email, String password);

    String encode(String rawPassword);
}
