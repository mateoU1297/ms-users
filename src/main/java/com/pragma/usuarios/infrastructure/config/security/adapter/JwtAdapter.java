package com.pragma.usuarios.infrastructure.config.security.adapter;

import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IJwtPort;
import com.pragma.usuarios.infrastructure.config.security.JwtUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAdapter implements IJwtPort {

    private final JwtUtils jwtUtils;

    @Override
    public String generateToken(User user) {
        return jwtUtils.generateJwtToken(user);
    }
}
