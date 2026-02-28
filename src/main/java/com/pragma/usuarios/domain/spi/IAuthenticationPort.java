package com.pragma.usuarios.domain.spi;

public interface IAuthenticationPort {

    void authenticate(String email, String password);
}
