package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.*;
import java.time.LocalDate;

@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class User {
    @PositiveOrZero(message = "id должен быть положительным целым числом")
    private Integer id;
    @NotBlank(message = "email не может быть пустым")
    @NotNull(message = "email не может равняться нулю")
    @Email(message = "email не валидный")
    private String email;
    @NotBlank(message = "login не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "login не может содержать пробелы")
    private String login;
    private String name;
    @NotNull(message = "birthday не может равняться нулю")
    @Past(message = "birthday не может быть в будущем")
    private LocalDate birthday;
}
