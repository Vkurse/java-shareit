package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.EntityAlreadyExist;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserJpaRepository repository;

    @InjectMocks
    private UserServiceImpl service;


    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
    }

    @Test
    void getUsers() {
        when(repository.findAll()).thenReturn(List.of(user));


        assertEquals(service.getUsers(), List.of(UserMapper.toUserDto(user)));
    }

    @Test
    void getUser() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        assertEquals(service.getUser(1L), UserMapper.toUserDto(user));

        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            service.getUser(10L);
        });
    }

    @Test
    void addUser() {
        when(repository.save(any())).thenReturn(user);

        assertEquals(service.addUser(UserMapper.toUserDto(user)), UserMapper.toUserDto(user));

        when(repository.existsUserByEmail(any())).thenReturn(true);
        assertThrows(EntityAlreadyExist.class, () -> {
            service.addUser(UserMapper.toUserDto(user));
        });

        user.setEmail(null);
        assertThrows(IllegalArgumentException.class, () -> {
            service.addUser(UserMapper.toUserDto(user));
        });
    }

    @Test
    void updateUser() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("update")
                .email("update@email.com")
                .build();

        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(user);

        assertEquals(service.updateUser(1L, userDto), UserMapper.toUserDto(user));

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            service.updateUser(1L, userDto);
        });


    }

    @Test
    void deleteUser() {
        when(repository.existsById(anyLong())).thenReturn(true);
        service.deleteUser(1L);
        verify(repository).deleteById(1L);

        when(repository.existsById(anyLong())).thenReturn(false);
        assertThrows(ObjectNotFoundException.class, () -> service.deleteUser(999L));
    }
}