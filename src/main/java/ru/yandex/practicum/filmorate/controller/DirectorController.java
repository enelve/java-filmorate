package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<DirectorDto> returnAll() {
        return directorService.getAll().stream().map(DirectorMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public DirectorDto findById(@PathVariable int id) {
        return DirectorMapper.toDto(directorService.findById(id));
    }

    @PostMapping
    public DirectorDto create(@Valid @RequestBody DirectorDto directorDto) {
        Director director = DirectorMapper.toEntity(directorDto);
        return DirectorMapper.toDto(directorService.save(director));
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody DirectorDto directorDto) {
        Director director = DirectorMapper.toEntity(directorDto);
        return DirectorMapper.toDto(directorService.update(director));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("DELETE запрос на удаление режиссёра с id: {}", id);
        directorService.delete(id);
    }


}
