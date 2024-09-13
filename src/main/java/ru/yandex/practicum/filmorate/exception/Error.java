package ru.yandex.practicum.filmorate.exception;

public enum Error {
    ERROR_0001("0001", "Данные по ID = %s не найдены."),
    ERROR_0002("0002", "Данные c ID уже существуют.");

    private final String errorCode;
    private final String errorMessage;

    Error(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String message() {
        return this.errorMessage;
    }
}
