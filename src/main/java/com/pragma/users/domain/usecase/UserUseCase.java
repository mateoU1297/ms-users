package com.pragma.users.domain.usecase;

import com.pragma.users.domain.api.IUserServicePort;
import com.pragma.users.domain.exception.UserUnderageException;
import com.pragma.users.domain.model.User;
import com.pragma.users.domain.spi.IAuthenticationPort;
import com.pragma.users.domain.spi.IUserPersistencePort;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IAuthenticationPort authenticationPort;

    public UserUseCase(IUserPersistencePort userPersistencePort, IAuthenticationPort authenticationPort) {
        this.userPersistencePort = userPersistencePort;
        this.authenticationPort = authenticationPort;
    }

    @Override
    public User findByEmail(String email) {
        return userPersistencePort.findByEmail(email);
    }

    @Override
    public User save(User user) {
        if (!user.isAdult())
            throw new UserUnderageException();

        user.setPassword(authenticationPort.encode(user.getPassword()));

        return userPersistencePort.save(user);
    }

    @Override
    public User findById(Long id) {
        return userPersistencePort.findById(id);
    }
}
