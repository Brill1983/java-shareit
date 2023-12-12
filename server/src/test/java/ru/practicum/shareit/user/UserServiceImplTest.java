package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ElementNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;


    @Test
    void createUser() {
        UserDto userDto = makeUserDto("Иван Иванович", "ii@mail.ru");

        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo((userDto.getEmail())));
    }

    @Test
    void updateUser() {
        UserDto userDto = makeUserDto("Иван Иванович", "ii@mail.ru");

        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo((userDto.getEmail())));

        UserDto userDtoForUpdate = makeUserDto("Петр Петрович", "pp@mail.ru");

        service.updateUser(userDtoForUpdate, 1L);

        query = em.createQuery("select u from User u where u.id = :id", User.class);
        user = query.setParameter("id", 1L)
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDtoForUpdate.getName()));
        assertThat(user.getEmail(), equalTo((userDtoForUpdate.getEmail())));
    }

    @Test
    void updateUserWithWrongId() {
        UserDto userDtoForUpdate = makeUserDto("Петр Петрович", "pp@mail.ru");
        try {
            service.updateUser(userDtoForUpdate, 2L);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
        }
    }

    @Test
    void getUserById() {
        UserDto userDto = makeUserDto("Иван Иванович", "ii@mail.ru");
        service.createUser(userDto);

        UserDto user = service.getUserById(1L);
        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo((userDto.getEmail())));
    }

    @Test
    void getUserByIdWithWrongUserId() {
        try {
            service.getUserById(2L);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
        }
    }

    @Test
    void deleteUser() {
        UserDto userDto = makeUserDto("Иван Иванович", "ii@mail.ru");
        service.createUser(userDto);

        service.deleteUser(1L);

        TypedQuery<User> query = em.createQuery("select u from User u where u.id = :id", User.class);
        try {
            query.setParameter("id", 1L)
                    .getSingleResult();
        } catch (NoResultException thrown) {
            assertThat(thrown.getMessage(), equalTo("No entity found for query"));
        }
    }

    @Test
    void deleteUserWithWrongId() {
        try {
            service.deleteUser(2L);
        } catch (ElementNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
        }
    }

    @Test
    void getAllUsers() {
        List<UserDto> users = List.of(
                makeUserDto("Иван Иванович", "ii@mail.ru"),
                makeUserDto("Петр Петрович", "pp@mail.ru")
        );
        for (UserDto user : users) {
            User entity = UserMapper.toUser(user);
            em.persist(entity);
        }
        em.flush();

        List<UserDto> receivedUsers = service.getAllUsers();

        assertThat(receivedUsers, hasSize(users.size()));
        for (UserDto user : users) {
            assertThat(receivedUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }
}