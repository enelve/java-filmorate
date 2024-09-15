package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0001;
import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0002;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public User add(User user) {
        Integer userId = user.getId();
        if (userStorage.existsById(userId)) {
            throw new DuplicateException(String.format(ERROR_0002.message(), userId));
        }
        user.setName(user.getName().isBlank() ? user.getLogin() : user.getName());
        log.info("Добавлена информация о новом пользователе: {}", user.getName());
        return userStorage.add(user);
    }

    public User update(User user) {
        Integer userId = user.getId();
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), userId));
        }
        userStorage.update(user);
        log.info("Изменена информация о пользователе: : {}:{}", userId, user.getName());
        return user;
    }

    public Collection<User> getAll() {
        return userStorage.findAll();
    }

    public User getUserById(Integer id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ERROR_0001.message(), id)));
    }

    public User addFriend(Integer id, Integer friendId) {
        User user = userStorage.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(ERROR_0001.message(), id)));
        User friend = userStorage.findById(friendId).orElseThrow(() ->
                new NotFoundException(String.format(ERROR_0001.message(), friendId)));
        user.getFriendsId().add(friend.getId());
        friend.getFriendsId().add(user.getId());
        userStorage.update(user);
        userStorage.update(friend);

        log.info("Пользователи {} и {} добавились в друзья", id, friendId);
        return user;
    }

    public User removeFriend(Integer id, Integer friendId) {
        User user = userStorage.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(ERROR_0001.message(), id)));
        User friend = userStorage.findById(friendId).orElseThrow(() ->
                new NotFoundException(String.format(ERROR_0001.message(), friendId)));
        user.getFriendsId().remove(friend.getId());
        friend.getFriendsId().remove(user.getId());
        userStorage.update(user);
        userStorage.update(friend);

        log.info("Пользователи {} и {} больше не друзья", id, friendId);
        return getUserById(id);
    }

    public List<User> getFriends(Integer id) {
        return userStorage.findById(id)
                .map(User::getFriendsId)
                .orElseThrow(() -> new NotFoundException("Id не найден")).stream()
                .map(this::getUserById).toList();
    }

    public List<User> getCommonFriends(Integer id, Integer friendId) {
        return Stream.of(getFriends(id), getFriends(friendId))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(p -> p.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();
    }
}
