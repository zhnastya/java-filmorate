package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.validator.MinimumDate;

import java.time.LocalDate;

@Data
@Slf4j
public class Film {
    @PositiveOrZero(message = "id должен быть положительным целым числом")
    private final int id;
    @NotEmpty(message = "название не может быть пустым")
    private final String name;
    @Size(max = 200, message = "длина описания не может быть больше 200 символов")
    private final String description;
    @NotNull(message = "дата не может быть null")
    @MinimumDate
    private final LocalDate releaseDate;
    @Positive(message = "продолжительность фильма должна быть положительной")
    private final int duration;
}
