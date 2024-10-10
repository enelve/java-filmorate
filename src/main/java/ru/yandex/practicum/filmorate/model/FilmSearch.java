package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmSearch {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private FilmRating mpa;
    private Set<Genre> genres = new HashSet<>();
    private List<Director> directors;
}