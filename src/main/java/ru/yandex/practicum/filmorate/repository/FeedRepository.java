package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.enums.EventTypesEnum;
import ru.yandex.practicum.filmorate.enums.OperationsEnum;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedRepository {
    void add(Integer userId, long entityId, EventTypesEnum eventTypes, OperationsEnum operations);

    List<Event> getFeed(Integer userId);
}
