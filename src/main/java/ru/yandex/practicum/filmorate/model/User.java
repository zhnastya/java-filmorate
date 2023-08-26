package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Data
@Slf4j
public class User {
    @PositiveOrZero(message = "id должен быть положительным целым числом")
    private final Integer id;
    @NotBlank(message = "email не может быть пустым")
    @NotNull(message = "email не может равняться нулю")
    @Email(message = "email не валидный")
    private final String email;
    @NotBlank(message = "login не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "login не может содержать пробелы")
    private final String login;
    private final String name;
    @NotNull(message = "birthday не может равняться нулю")
    @Past(message = "birthday не может быть в будущем")
    private final LocalDate birthday;
}
