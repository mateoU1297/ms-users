package com.pragma.usuarios.domain.usecase;

import com.pragma.usuarios.domain.api.IAuthenticationServicePort;
import com.pragma.usuarios.domain.spi.IAuthenticationPort;

public class AuthenticationUseCase implements IAuthenticationServicePort {

    private final IAuthenticationPort authenticationPort;

    public AuthenticationUseCase(IAuthenticationPort authenticationPort) {
        this.authenticationPort = authenticationPort;
    }

    @Override
    public void authenticate(String email, String password) {
        authenticationPort.authenticate(email, password);
    }
}
