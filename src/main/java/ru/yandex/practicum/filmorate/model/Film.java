package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.validator.MinimumDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class Film {
    @PositiveOrZero(message = "id должен быть положительным целым числом")
    private Integer id;
    @NotEmpty(message = "название не может быть пустым")
    private String name;
    @Size(max = 200, message = "длина описания не может быть больше 200 символов")
    private String description;
    @NotNull(message = "дата не может быть null")
    @MinimumDate
    private LocalDate releaseDate;
    @Positive(message = "продолжительность фильма должна быть положительной")
    private int duration;
    private int likes;
}
