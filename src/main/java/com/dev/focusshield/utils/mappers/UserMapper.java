package com.dev.focusshield.utils.mappers;

import com.dev.focusshield.dto.UserDto;
import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.model.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "username",target = "username")
    @Mapping(source = "email",target = "email")
    UserDto userEntityToUserDto(UserEntity user);


    @Mapping(source = "username",target = "username")
    @Mapping(source = "email",target = "email")
    @Mapping(source = "password",target = "password")
    UserEntity registerRequestToUserEntity (RegisterRequest request);

}
