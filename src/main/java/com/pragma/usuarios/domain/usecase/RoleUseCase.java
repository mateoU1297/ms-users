package com.pragma.usuarios.domain.usecase;

import com.pragma.usuarios.domain.api.IRoleServicePort;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.enums.RoleName;
import com.pragma.usuarios.domain.spi.IRolePersistencePort;

public class RoleUseCase implements IRoleServicePort {

    private final IRolePersistencePort rolePersistencePort;

    public RoleUseCase(IRolePersistencePort rolePersistencePort) {
        this.rolePersistencePort = rolePersistencePort;
    }

    @Override
    public Role getRoleByName(RoleName roleName) {
        return rolePersistencePort.getRoleByName(roleName);
    }
}
