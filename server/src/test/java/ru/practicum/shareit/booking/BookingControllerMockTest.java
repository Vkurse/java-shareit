package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerMockTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingResponseDto bookingDtoResponse;

    private BookingRequestDto bookingDtoRequest;

    @BeforeEach
    void init() {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("testName")
                .email("test@mail.ru")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("testName")
                .description("testDescription")
                .available(true)
                .build();

        bookingDtoResponse = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 10, 20, 10, 0))
                .end(LocalDateTime.of(2023, 12, 20, 10, 0))
                .booker(userDto)
                .item(itemDto)
                .build();

        bookingDtoRequest = BookingRequestDto.builder()
                .start(LocalDateTime.of(2023, 10, 20, 10, 0))
                .end(LocalDateTime.of(2023, 12, 20, 10, 0))
                .itemId(1L)
                .build();
    }

    @Test
    void saveTest() throws Exception {
        when(bookingService.create(any(BookingRequestDto.class), Mockito.anyLong()))
                .thenReturn(bookingDtoResponse);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));
    }

    @Test
    void saveTestBookingStartAfterEnd() throws Exception {
        bookingDtoRequest.setStart(LocalDateTime.now().plusDays(5));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusDays(2));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateStatusTest() throws Exception {
        bookingDtoResponse.setStatus(Status.APPROVED);
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoResponse);
        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));
    }

    @Test
    void findAllByOwnerIdTest() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoResponse));
        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoResponse))));
    }

    @Test
    void findByIdTest() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDtoResponse);
        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));
    }
}