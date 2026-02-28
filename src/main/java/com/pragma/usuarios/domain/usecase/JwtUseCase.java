package com.pragma.usuarios.domain.usecase;

import com.pragma.usuarios.domain.api.IJwtServicePort;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IJwtPort;

public class JwtUseCase implements IJwtServicePort {

    private final IJwtPort jwtPort;

    public JwtUseCase(IJwtPort jwtPort) {
        this.jwtPort = jwtPort;
    }

    @Override
    public String generateToken(User user) {
        return jwtPort.generateToken(user);
    }
}
