package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventTypesEnum;
import ru.yandex.practicum.filmorate.enums.OperationsEnum;
import ru.yandex.practicum.filmorate.exception.NotContentException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.*;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0001;
import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0002;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FeedRepository feedRepository;
    private static final EventTypesEnum EVENT_TYPES = EventTypesEnum.FRIEND;
    private final FilmRatingRepository filmRatingRepository;
    private final FilmRepository filmRepository;

    @Autowired
    public UserService(@Qualifier("userDatabaseRepository") UserRepository userRepository,
                       FriendshipRepository friendshipRepository, FeedRepository feedRepository,
                       FilmRatingRepository filmRatingRepository,
                       @Qualifier("filmDatabaseRepository") FilmRepository filmRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.feedRepository = feedRepository;
        this.filmRatingRepository = filmRatingRepository;
        this.filmRepository = filmRepository;
    }

    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    public User getById(Integer id) {
        log.info("получение пользователья по id {}", id);
        if (!userRepository.exists(id)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
        return userRepository.getById(id);
    }

    public User add(User user) {
        Integer userId = user.getId();
        if (userRepository.exists(userId)) {
            throw new DuplicateException(String.format(ERROR_0002.message(), userId));
        }
        user.setName(user.getName().isBlank() ? user.getLogin() : user.getName());
        log.info("Добавлена информация о новом пользователе: {}", user.getName());
        return userRepository.add(user);
    }

    public User update(User user) {
        Integer userId = user.getId();
        if (!userRepository.exists(userId)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), userId));
        }
        userRepository.update(user);
        log.info("Изменена информация о пользователе: : {}:{}", userId, user.getName());
        return user;
    }

    public User addFriend(Integer id, Integer friendId) {
        log.info("Добавляем пользователю с id {}, друга с id {}", id, friendId);
        List<Integer> idList = getAll().stream().map(User::getId).toList();
        if (Objects.equals(id, friendId)) {
            throw new DuplicateException(String.format(ERROR_0002.message(), id));
        }
        if (!idList.contains(id)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
        if (!idList.contains(friendId)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), friendId));
        }
        if (!friendshipRepository.exist(id, friendId)) {
            friendshipRepository.addFriend(id, friendId, true);
            feedRepository.add(id, friendId, EVENT_TYPES, OperationsEnum.ADD);
        }
        return userRepository.getById(id);
    }

    public User removeFriend(Integer id, Integer friendId) {
        log.info("Удаляем у пользователя с id {}, друга с id {}", id, friendId);
        if (!userRepository.exists(id) || !userRepository.exists(friendId)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        } else if (!friendshipRepository.exist(id, friendId)) {
            throw new NotContentException(String.format(ERROR_0001.message(), id));
        }

        friendshipRepository.deleteFriend(id, friendId);
        feedRepository.add(id, friendId, EVENT_TYPES, OperationsEnum.REMOVE);
        return userRepository.getById(id);
    }

    public List<User> getFriends(Integer id) {
        if (userRepository.exists(id)) {
            return friendshipRepository.getFriends(id).stream().map(userRepository::getById).toList();
        } else {
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
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

    public Collection<Film> getRecommendations(Integer id) {
        Collection<Film> films = userRepository.getRecommendations(id);
        for (Film f : films) {
            f.setFilmRating(filmRatingRepository.getById(f.getFilmRating().getId()));
            for (Genre g : filmRepository.getGenres(f.getId())) {
                f.getGenres().add(g);
            }
        }
        return films;
    }

    public void delete(Integer id) {
        log.info("Удаление пользователя {}", id);
        if (!userRepository.exists(id)) {
            log.error("Пользователь с Id={} не найден!", id);
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
        userRepository.delete(id);
        log.debug("Пользователь {} удален.", id);
    }

    public List<Event> getFeedList(Integer id) {
        log.info("Запрос списка ленты событий для пользователя {}", id);
        if (!userRepository.exists(id)) {
            log.error("Пользователь с Id={} не найден!", id);
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
        return feedRepository.getFeed(id);
    }
}
