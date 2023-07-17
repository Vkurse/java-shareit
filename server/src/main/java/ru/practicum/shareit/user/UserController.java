package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Пришел /GET запрос на получение всех пользователей");
        List<UserDto> users = userService.getAll();
        log.info("Ответ отправлен {}", users);
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Пришел /GET запрос на получение пользователя по id {}", id);
        UserDto user = userService.getById(id);
        log.info("Ответ отправлен {}", user);
        return user;
    }

    @PostMapping
    public UserDto create(@RequestBody User user) {
        log.info("Пришел /POST запрос на создание пользователя {}", user);
        UserDto createdUser = userService.create(user);
        log.info("Ответ отправлен {}", createdUser);
        return createdUser;
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody User user, @PathVariable Long id) {
        log.info("Пришел /PATCH запрос на изменение данных пользователя с id {}", id);
        UserDto updatedUser = userService.update(user, id);
        log.info("Ответ отправлен {}", updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Пришел /DELETE запрос на удаление пользователя с id {}", id);
        userService.delete(id);
        log.info("Пользователь удален ");
    }
}
