package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0001;
import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0002;

@Service
@Slf4j
public class DirectorService {
    DirectorRepository directorRepository;

    @Autowired
    public DirectorService(@Qualifier("DirectorDatabaseRepository") DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public Director save(Director director) {
        Integer directorId = director.getId();
        if (directorRepository.exists(directorId)) {
            throw new DuplicateException(String.format(ERROR_0002.message(), directorId));
        }
        Director savedDirector = directorRepository.save(director);
        log.info("Добавлена информация о новом режиссере: {}", savedDirector.getName());
        return savedDirector;
    }

    public Director update(Director director) {
        Integer directorId = director.getId();

        if (!directorRepository.exists(directorId)) {
            throw new NotFoundErrorException(String.format(ERROR_0002.message(), directorId));
        }
        Director savedDirector = directorRepository.update(director);
        log.info("Изменена информация о режиссере: {}", savedDirector.getName());
        return savedDirector;
    }

    public Collection<Director> getAll() {
        log.info("Получение списка всех режиссеров");
        return directorRepository.findAll();
    }

    public Director findById(Integer id) {
        if (!directorRepository.exists(id)) {
            throw new NotFoundErrorException(String.format(ERROR_0001.message(), id));
        }
        log.info("Получение информации о режиссере по id: {}", id);
        return directorRepository.findById(id);
    }

    public void delete(Integer id) {
        if (!directorRepository.exists(id)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
        directorRepository.delete(id);
    }
}
