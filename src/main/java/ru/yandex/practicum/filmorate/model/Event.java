package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.enums.EventTypesEnum;
import ru.yandex.practicum.filmorate.enums.OperationsEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @PastOrPresent
    private long timestamp;
    @NotNull
    private long userId;
    @NotNull
    private EventTypesEnum eventType;
    @NotNull
    private OperationsEnum operation;
    private long eventId;
    @NotNull
    private long entityId;
}
