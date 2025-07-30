package com.cmms.lite.security.mapper;

import com.cmms.lite.security.dto.UserSummaryDto;
import com.cmms.lite.security.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserSummaryDto toSummaryDto(User user);
}