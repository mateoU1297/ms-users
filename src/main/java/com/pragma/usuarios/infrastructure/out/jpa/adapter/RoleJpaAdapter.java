package com.pragma.usuarios.infrastructure.out.jpa.adapter;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.enums.RoleName;
import com.pragma.usuarios.domain.spi.IRolePersistencePort;
import com.pragma.usuarios.infrastructure.mapper.IRoleEntityMapper;
import com.pragma.usuarios.infrastructure.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoleJpaAdapter implements IRolePersistencePort {

    private final RoleRepository roleRepository;
    private final IRoleEntityMapper roleEntityMapper;

    @Override
    public Role getRoleByName(RoleName roleName) {
        return roleEntityMapper.toRole(roleRepository.findByName(roleName));
    }
}
