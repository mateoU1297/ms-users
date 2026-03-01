package com.pragma.usuarios.domain.spi;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.enums.RoleName;

public interface IRolePersistencePort {

    Role getRoleByName(RoleName roleName);
}
