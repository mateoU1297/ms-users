package com.pragma.usuarios.domain.spi;

import com.pragma.usuarios.domain.model.User;

public interface IJwtPort {

    String generateToken(User user);
}
