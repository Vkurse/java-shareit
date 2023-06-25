package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserInfoDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;

    private BookingInfoDto bookingInfoDto;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingInfoDto = BookingInfoDto.builder()
                .id(1L)
                .item(ItemInfoDto.builder().id(20L).name("Item").build())
                .booker(UserInfoDto.builder()
                        .id(10L)
                        .build())
                .start(LocalDateTime.of(2023, Month.JULY, 1, 12, 0))
                .end(LocalDateTime.of(2023, Month.JULY, 2, 12, 0))
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .bookerId(10L)
                .itemId(20L)
                .start(LocalDateTime.of(2023, Month.JULY, 1, 12, 0))
                .end(LocalDateTime.of(2023, Month.JULY, 2, 12, 0))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingInfoDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInfoDto))
                        .header("X-Sharer-User-Id", 10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingInfoDto)));
    }

    @Test
    void updateBookingStatus() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingInfoDto);

        mockMvc.perform(patch("/bookings" + "/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingInfoDto)));
    }

    @Test
    void getCurrentBooking() throws Exception {
        when(bookingService.getCurrentBooking(anyLong(), anyLong()))
                .thenReturn(bookingInfoDto);

        mockMvc.perform(get("/bookings" + "/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingInfoDto)));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingInfoDto));

        mockMvc.perform(get("/bookings" + "?state=ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingInfoDto))));

        mockMvc.perform(get("/bookings" + "?state=ALL&size=-10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnerBooking() throws Exception {
        when(bookingService.getOwnerBooking(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingInfoDto));

        mockMvc.perform(get("/bookings" + "/owner?state=ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingInfoDto))));

        mockMvc.perform(get("/bookings" + "/owner?state=ALL&size=-10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}