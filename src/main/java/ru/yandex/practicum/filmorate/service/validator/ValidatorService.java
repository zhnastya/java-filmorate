package ru.yandex.practicum.filmorate.service.validator;

import ru.yandex.practicum.filmorate.exeption.ValidationException;

public class ValidatorService {

    public static void validateId(Integer id) {
        if (id == null) throw new ValidationException("Значение id не может равняться null");
        if (id < 0) throw new ValidationException("Значение id не может быть меньше 0");
    }
}
