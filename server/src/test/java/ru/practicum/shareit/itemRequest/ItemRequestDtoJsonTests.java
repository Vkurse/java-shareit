package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponseDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTests {
    @Autowired
    JacksonTester<ItemRequestResponseDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestResponseDto itemRequestDto = ItemRequestResponseDto
                .builder()
                .id(1L)
                .description("descriptionOfItemRequest")
                .build();

        JsonContent<ItemRequestResponseDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("descriptionOfItemRequest");
    }
}
