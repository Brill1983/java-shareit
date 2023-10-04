package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.service.Constants.HEADER;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private CommentDto commentDto;
    private ResponseEntity<Object> itemResponse;
    private ResponseEntity<Object> commentResponse;

    @BeforeEach
    public void itemCreate() {
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, null);
        commentDto = new CommentDto(1L, "Коммент 1", itemDto, "Иван Иванович", LocalDateTime.now());
        itemResponse = new ResponseEntity<>(itemDto, HttpStatus.OK);
        commentResponse = new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @Test
    void saveItem() throws Exception {

        when(itemClient.createItem(anyLong(), any()))
                .thenReturn(itemResponse);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));

        verify(itemClient, times(1))
                .createItem(anyLong(), any());
    }

    @Test
    void saveItemWithEmptyName() throws Exception {
        itemDto.setName("");

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(itemClient, never())
                .createItem(anyLong(), any());
    }

    @Test
    void saveItemWithNullName() throws Exception {
        itemDto.setName(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(itemClient, never())
                .createItem(anyLong(), any());
    }

    @Test
    void saveItemWithEmptyDescription() throws Exception {
        itemDto.setDescription("");

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(itemClient, never())
                .createItem(anyLong(), any());
    }

    @Test
    void saveItemWithNullDescription() throws Exception {
        itemDto.setDescription(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(itemClient, never())
                .createItem(anyLong(), any());
    }

    @Test
    void saveItemWithNullAvailable() throws Exception {
        itemDto.setAvailable(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(itemClient, never())
                .createItem(anyLong(), any());
    }

    @Test
    void updateItem() throws Exception {
        when(itemClient.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(itemResponse);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));

        verify(itemClient, times(1))
                .updateItem(anyLong(), any(), anyLong());
    }

    @Test
    void getItemById() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(itemResponse);

        mvc.perform(get("/items/1")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));

        verify(itemClient, times(1))
                .getItemById(anyLong(), anyLong());
    }

    @Test
    void getUserItemsWithNoParams() throws Exception {
        List<ItemDto> itemList = List.of(itemDto);
        ResponseEntity<Object> responseWithList = new ResponseEntity<>(itemList, HttpStatus.OK);

        when(itemClient.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(responseWithList);

        mvc.perform(get("/items")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemList)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));

        verify(itemClient, times(1))
                .getUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getUserItemsWithParams() throws Exception {
        int from = 0;
        int size = 2;
        List<ItemDto> itemList = List.of(itemDto);
        ResponseEntity<Object> responseWithList = new ResponseEntity<>(itemList, HttpStatus.OK);
        when(itemClient.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(responseWithList);
        mvc.perform(get("/items")
                        .header(HEADER, 1L)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemList)));

        verify(itemClient, times(1))
                .getUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getUserItemsWithBadFromParam() throws Exception {
        int from = -1;
        int size = 2;

        mvc.perform(get("/items")
                        .header(HEADER, 1L)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never())
                .getUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getUserItemsWithNegativeSizeParam() throws Exception {
        int from = 0;
        int size = -2;

        mvc.perform(get("/items")
                        .header(HEADER, 1L)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never())
                .getUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getUserItemsWithNullSizeParam() throws Exception {
        int from = 0;
        int size = 0;

        mvc.perform(get("/items")
                        .header(HEADER, 1L)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never())
                .getUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void search() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 2;
        String text = "вещ";
        List<ItemDto> itemList = List.of(itemDto);
        ResponseEntity<Object> responseWithList = new ResponseEntity<>(itemList, HttpStatus.OK);
        when(itemClient.search(text, userId))
                .thenReturn(responseWithList);
        mvc.perform(get("/items/search")
                        .header(HEADER, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemList)));

        verify(itemClient, times(1))
                .search(text, userId);
    }

    @Test
    void saveComment() throws Exception {
        when(itemClient.saveComment(anyLong(), anyLong(), any()))
                .thenReturn(commentResponse);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(itemClient, times(1))
                .saveComment(anyLong(), anyLong(), any());
    }
}