package com.boiko.data_service.mapper;

import com.boiko.data_service.dto.UserDetailsDTO;
import com.boiko.data_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDetailsDTO modelToDTO(User model) {
        return new UserDetailsDTO(
                model.getId(),
                model.getNickname(),
                model.getEmail(),
                model.getBirthday(),
                model.getAvatar().getUrl()
        );
    }
}
