package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemServiceImpl itemService;

    private ItemDto itemDto;
    private ItemInfoDto itemInfoDto;
    private CommentDto commentDto;


    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();

        itemInfoDto = ItemInfoDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .comments(new ArrayList<>())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("commentText")
                .authorName("commentAuthorName")
                .build();
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(1L))
                .thenReturn(List.of(itemInfoDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("itemName")))
                .andExpect(jsonPath("$[0].description", is("itemDescription")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].comments", is(Collections.emptyList())));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemInfoDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("itemName")))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.comments", is(Collections.emptyList())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("itemName")))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(new ObjectMapper().writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("commentText")))
                .andExpect(jsonPath("$.authorName", is("commentAuthorName")));
    }

    @Test
    void updateItem() throws Exception {

        ItemDto updatedItem = itemDto;
        updatedItem.setName("update");
        updatedItem.setDescription("update");

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(updatedItem);


        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(new ObjectMapper().writeValueAsString(updatedItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("update")))
                .andExpect(jsonPath("$.description", is("update")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items" + "/1"))
                .andExpect(status().isOk());
        verify(itemService, times(1))
                .deleteItem(anyLong());
    }

    @Test
    void searchItems() throws Exception {

        ItemDto secondItem = ItemDto.builder()
                .id(2L)
                .name("item2Name")
                .description("item2Description")
                .available(true)
                .build();

        when(itemService.searchItems("description"))
                .thenReturn(List.of(itemDto, secondItem));


        mockMvc.perform(get("/items/search")
                        .param("text", "description")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("itemName")))
                .andExpect(jsonPath("$[0].description", is("itemDescription")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("item2Name")))
                .andExpect(jsonPath("$[1].description", is("item2Description")))
                .andExpect(jsonPath("$[1].available", is(true)));

    }
}