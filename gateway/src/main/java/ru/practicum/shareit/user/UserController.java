package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Пришел /GET запрос на получение всех пользователей");
        ResponseEntity<Object> users = userClient.getALl();
        log.info("Ответ отправлен {}", users);
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Пришел /GET запрос на получение пользователя по id {}", id);
        ResponseEntity<Object> user = userClient.getById(id);
        log.info("Ответ отправлен {}", user);
        return user;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserRequestDto user) {
        log.info("Пришел /POST запрос на создание пользователя {}", user);
        ResponseEntity<Object> createdUser = userClient.create(user);
        log.info("Ответ отправлен {}", createdUser);
        return createdUser;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody UserRequestDto user, @PathVariable Long id) {
        log.info("Пришел /PATCH запрос на изменение данных пользователя с id {}", id);
        ResponseEntity<Object> updatedUser = userClient.update(id, user);
        log.info("Ответ отправлен {}", updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Пришел /DELETE запрос на удаление пользователя с id {}", id);
        ResponseEntity<Object> deletedUser = userClient.delete(id);
        log.info("Пользователь {} удален", deletedUser);
        return deletedUser;
    }
}
