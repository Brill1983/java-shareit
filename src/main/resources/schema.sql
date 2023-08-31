CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(100),
                                     email VARCHAR(50) NOT NULL,
                                     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
                                     item_id      bigint generated by default as identity primary key,
                                     name        varchar(150)  not null,
                                     description varchar(1000) not null,
                                     available   boolean       not null,
                                     owner       bigint        not null
                                         constraint items_users_user_id_fk
                                             references users
                                 );
