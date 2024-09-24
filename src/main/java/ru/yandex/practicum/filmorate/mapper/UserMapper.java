package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@UtilityClass
public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
    }

    public static User toEntity(UserDto userDto) {
        return new User(
                userDto.id(),
                userDto.email(),
                userDto.login(),
                userDto.name(),
                userDto.birthday()
        );
    }
}
