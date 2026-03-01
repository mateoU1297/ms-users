package com.pragma.usuarios.domain.api;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.enums.RoleName;

public interface IRoleServicePort {

    Role getRoleByName(RoleName roleName);
}
