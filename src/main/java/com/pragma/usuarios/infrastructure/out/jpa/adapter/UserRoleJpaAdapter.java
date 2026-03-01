package com.pragma.usuarios.infrastructure.out.jpa.adapter;

import com.pragma.usuarios.domain.model.UserRole;
import com.pragma.usuarios.domain.spi.IUserRolePersistencePort;
import com.pragma.usuarios.infrastructure.mapper.IUserRoleEntityMapper;
import com.pragma.usuarios.infrastructure.out.jpa.entity.RolEntity;
import com.pragma.usuarios.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.usuarios.infrastructure.out.jpa.entity.UserRoleEntity;
import com.pragma.usuarios.infrastructure.repository.RoleRepository;
import com.pragma.usuarios.infrastructure.repository.UserRepository;
import com.pragma.usuarios.infrastructure.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRoleJpaAdapter implements IUserRolePersistencePort {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final IUserRoleEntityMapper userRoleEntityMapper;

    @Override
    public UserRole save(UserRole userRole) {
        UserRoleEntity userRoleEntity = userRoleEntityMapper.toEntity(userRole);

        UserEntity userEntity = userRepository.getReferenceById(userRole.getUserId());
        RolEntity rolEntity = roleRepository.getReferenceById(userRole.getRoleId());

        userRoleEntity.setUser(userEntity);
        userRoleEntity.setRole(rolEntity);
        return userRoleEntityMapper.toDomain(userRoleRepository.save(userRoleEntity));
    }
}
