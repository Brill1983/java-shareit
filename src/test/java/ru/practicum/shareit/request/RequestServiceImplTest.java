package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exceptions.ElementNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RequestServiceImplTest {

    private final EntityManager em;

    private final RequestService requestService;

    private final ItemService itemService;

    private final UserService userService;

    private ItemDto itemDto;
    private UserDto userDto;
    private UserDto userDto2;
    private RequestDto requestDto;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, 1L);
        userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        userDto2 = new UserDto(2L, "Петр Петрович", "pp@mail.ru");
        requestDto = new RequestDto(1L, "Описание запроса 1", LocalDateTime.now(), null);

        userService.createUser(userDto);
        userService.createUser(userDto2);
    }

    @Test
    void createItemRequest() {

        RequestDto methodRequest = requestService.createItemRequest(userDto2.getId(), requestDto);

        TypedQuery<Request> query = em.createQuery("select r from Request r where r.id = :id", Request.class);
        Request dbRequest = query.setParameter("id", requestDto.getId())
                .getSingleResult();

        assertThat(methodRequest.getId(), equalTo(dbRequest.getId()));
        assertThat(methodRequest.getDescription(), equalTo(dbRequest.getDescription()));
        assertThat(methodRequest.getCreated(), notNullValue());
        assertThat(methodRequest.getItems(), nullValue());
    }

    @Test
    void createItemRequestWithWrongUserId() {
        long userId = 3L;

        try {
            requestService.createItemRequest(userId, requestDto);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + userId + " не зарегистрирован"));
        }
    }


    @Test
    void getUserItemRequests() {
        long userId = 2L;
        requestService.createItemRequest(userId, requestDto); // создали запрос на вещь 1 от User 2
        itemService.createItem(userDto.getId(), itemDto); // создали вещь 1 принадлежиащую User 1

        List<RequestDto> methodRequestList = requestService.getUserItemRequests(userId);

        TypedQuery<Request> query = em.createQuery("select r from Request r where r.user.id = :id", Request.class);
        List<Request> dbRequestList = query.setParameter("id", userId)
                .getResultList();

        assertThat(methodRequestList.size(), equalTo(dbRequestList.size()));
        assertThat(methodRequestList.get(0).getId(), equalTo(dbRequestList.get(0).getId()));
        assertThat(methodRequestList.get(0).getDescription(), equalTo(dbRequestList.get(0).getDescription()));
        assertThat(methodRequestList.get(0).getCreated(), notNullValue());

        List<Long> itemsRequestIds = List.of(itemDto.getRequestId());

        TypedQuery<Item> query1 = em.createQuery("select i from Item i where i.request.id in :id", Item.class);
        List<Item> dbItems = query1.setParameter("id", itemsRequestIds)
                .getResultList();

        assertThat(methodRequestList.get(0).getItems().get(0).getId(), equalTo(dbItems.get(0).getId()));
        assertThat(methodRequestList.get(0).getItems().get(0).getName(), equalTo(dbItems.get(0).getName()));
        assertThat(methodRequestList.get(0).getItems().get(0).getDescription(), equalTo(dbItems.get(0).getDescription()));
        assertThat(methodRequestList.get(0).getItems().get(0).getAvailable(), equalTo(dbItems.get(0).getAvailable()));
        assertThat(methodRequestList.get(0).getItems().get(0).getRequestId(), equalTo(dbItems.get(0).getRequest().getId()));
    }

    @Test
    void getItemRequestsFromOtherUsers() {
        int from = 0;
        int size = 5;
        UserDto userDto3 = new UserDto(3L, "Семен Семенович", "cc@mail.ru");
        userService.createUser(userDto3);
        requestService.createItemRequest(userDto2.getId(), requestDto); // создали запрос на вещь 1 от User 2
        itemService.createItem(userDto.getId(), itemDto);

        List<RequestDto> methodRequestList = requestService.getItemRequestsFromOtherUsers(userDto3.getId(), from, size);

        TypedQuery<Request> query = em.createQuery("select r from Request r where r.user.id <> :id", Request.class);
        List<Request> dbRequestList = query.setParameter("id", userDto3.getId())
                .getResultList();

        assertThat(methodRequestList.size(), equalTo(dbRequestList.size()));
        assertThat(methodRequestList.get(0).getId(), equalTo(dbRequestList.get(0).getId()));
        assertThat(methodRequestList.get(0).getDescription(), equalTo(dbRequestList.get(0).getDescription()));
        assertThat(methodRequestList.get(0).getCreated(), notNullValue());

        TypedQuery<Item> query1 = em.createQuery("select i from Item i where i.request.user.id <> :id", Item.class);
        List<Item> dbItems = query1.setParameter("id", userDto3.getId())
                .getResultList();

        assertThat(methodRequestList.get(0).getItems().get(0).getId(), equalTo(dbItems.get(0).getId()));
        assertThat(methodRequestList.get(0).getItems().get(0).getName(), equalTo(dbItems.get(0).getName()));
        assertThat(methodRequestList.get(0).getItems().get(0).getDescription(), equalTo(dbItems.get(0).getDescription()));
        assertThat(methodRequestList.get(0).getItems().get(0).getAvailable(), equalTo(dbItems.get(0).getAvailable()));
        assertThat(methodRequestList.get(0).getItems().get(0).getRequestId(), equalTo(dbItems.get(0).getRequest().getId()));
    }

    @Test
    void getItemRequestsFromOtherUsersIfUserIsRequester() {
        int from = 0;
        int size = 5;
        requestService.createItemRequest(userDto2.getId(), requestDto); // создали запрос на вещь 1 от User 2
        itemService.createItem(userDto.getId(), itemDto);

        List<RequestDto> methodRequestList = requestService.getItemRequestsFromOtherUsers(userDto2.getId(), from, size);

        assertThat(methodRequestList.size(), equalTo(0));
    }

    @Test
    void getOneItemRequest() {
        long userId = 1L;
        long requestId = 1L;
        requestService.createItemRequest(userDto2.getId(), requestDto); // создали запрос на вещь 1 от User 2
        itemService.createItem(userDto.getId(), itemDto);

        RequestDto methodRequest = requestService.getOneItemRequest(userId, requestId);

        TypedQuery<Request> query = em.createQuery("select r from Request r where r.id = :id", Request.class);
        Request dbRequest = query.setParameter("id", requestId)
                .getSingleResult();

        assertThat(methodRequest.getId(), equalTo(dbRequest.getId()));
        assertThat(methodRequest.getDescription(), equalTo(dbRequest.getDescription()));
        assertThat(methodRequest.getCreated(), notNullValue());

        TypedQuery<Item> query1 = em.createQuery("select i from Item i where i.request.id = :id", Item.class);
        List<Item> dbItems = query1.setParameter("id", requestId)
                .getResultList();

        assertThat(methodRequest.getItems().size(), equalTo(dbItems.size()));
        assertThat(methodRequest.getItems().get(0).getId(), equalTo(dbItems.get(0).getId()));
        assertThat(methodRequest.getItems().get(0).getName(), equalTo(dbItems.get(0).getName()));
        assertThat(methodRequest.getItems().get(0).getDescription(), equalTo(dbItems.get(0).getDescription()));
        assertThat(methodRequest.getItems().get(0).getAvailable(), equalTo(dbItems.get(0).getAvailable()));
        assertThat(methodRequest.getItems().get(0).getRequestId(), equalTo(dbItems.get(0).getRequest().getId()));
    }
}