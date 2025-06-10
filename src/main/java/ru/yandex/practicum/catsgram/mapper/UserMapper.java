package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.UserDto;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRegistrationDate(Instant.now());
        return dto;
    }
}
