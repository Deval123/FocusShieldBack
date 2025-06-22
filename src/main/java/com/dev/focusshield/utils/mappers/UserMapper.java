package com.dev.focusshield.utils.mappers;

import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.model.RegisterRequest;
import com.dev.focusshield.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = RoleMapper.class)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "firstname",target = "firstname")
    @Mapping(source = "surname",target = "surname")
    @Mapping(source = "username",target = "username")
    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "email",target = "email")
    @Mapping(source = "password",target = "password")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(source = "status",target = "status")
    @Mapping(source = "dateOfBirth",target = "dateOfBirth")
    @Mapping(source = "roles", target = "roles")
    User userEntityToUser(UserEntity userEntity);


    @Mapping(source = "username",target = "username")
    @Mapping(source = "firstname",target = "firstname")
    @Mapping(source = "surname",target = "surname")
    @Mapping(source = "email",target = "email")
    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "dateOfBirth",target = "dateOfBirth")
    @Mapping(source = "password",target = "password")
    UserEntity registerRequestToUserEntity (RegisterRequest request);

}
