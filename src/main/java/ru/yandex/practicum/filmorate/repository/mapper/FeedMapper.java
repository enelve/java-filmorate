package ru.yandex.practicum.filmorate.repository.mapper;

import jakarta.validation.ValidationException;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.enums.EventTypesEnum;
import ru.yandex.practicum.filmorate.enums.OperationsEnum;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {

        Event event = new Event();
        event.setTimestamp(rs.getTimestamp("time_stamp").getTime());
        event.setUserId(rs.getInt("user_id"));
        event.setEventId(rs.getInt("event_id"));
        event.setEntityId(rs.getInt("entity_id"));
        event.setEventType(setEventTypeEnum(rs.getString("event_type")));
        event.setOperation(setOperationsTypeEnum(rs.getString("operation")));
        return event;
    }

    private EventTypesEnum setEventTypeEnum(String eventType) {
        switch (eventType) {
            case "LIKE":
                return EventTypesEnum.LIKE;
            case "REVIEW":
                return EventTypesEnum.REVIEW;
            case "FRIEND":
                return EventTypesEnum.FRIEND;
            default:
                throw new ValidationException(String.format("Такой операции не существует: %s", eventType));
        }
    }

    private OperationsEnum setOperationsTypeEnum(String operationType) {
        switch (operationType) {
            case "ADD":
                return OperationsEnum.ADD;
            case "UPDATE":
                return OperationsEnum.UPDATE;
            case "REMOVE":
                return OperationsEnum.REMOVE;
            default:
                throw new ValidationException(String.format("Такой операции не существует: %s", operationType));
        }
    }
}