package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

//    @Override //TODO проверить необходимость
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof User)) return false;
//        return id != null && id.equals(((User) o).getId());
//    }
//
//    @Override //TODO проверить необходимость
//    public int hashCode() {
//        return getClass().hashCode();
//    }
}
