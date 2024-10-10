package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll().stream().map(UserMapper::toDto).toList();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userService.add(user));
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userService.update(user));
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        return UserMapper.toDto(userService.getById(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return UserMapper.toDto(userService.addFriend(id, friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDto removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return UserMapper.toDto(userService.removeFriend(id, friendId));
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id).stream().map(UserMapper::toDto).toList();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId).stream().map(UserMapper::toDto).toList();
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeedList(@PathVariable Integer id) {
        return userService.getFeedList(id);
    }
}
