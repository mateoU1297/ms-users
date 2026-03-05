package com.pragma.users.domain.usecase;

import com.pragma.users.domain.api.IUserRoleServicePort;
import com.pragma.users.domain.model.UserRole;
import com.pragma.users.domain.spi.IUserRolePersistencePort;

public class UserRoleUseCase implements IUserRoleServicePort {

    private final IUserRolePersistencePort userRolePersistencePort;

    public UserRoleUseCase(IUserRolePersistencePort userRolePersistencePort) {
        this.userRolePersistencePort = userRolePersistencePort;
    }

    @Override
    public UserRole save(Long ownerId, Long roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(ownerId);
        userRole.setRoleId(roleId);
        return userRolePersistencePort.save(userRole);
    }
}
