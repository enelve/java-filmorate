package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;

public interface FriendshipRepository {
    void addFriend(Integer userId, Integer friendId, boolean isFriend);

    void deleteFriend(Integer userId, Integer friendId);

    Collection<Integer> getFriends(Integer userId);

    Friendship getFriend(Integer userId, Integer friendId);

    boolean exist(Integer userId, Integer friendId);
}
