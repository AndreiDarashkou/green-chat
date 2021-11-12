drop schema if exists green_chat cascade;

create schema green_chat;

create table green_chat.users
(
    id       bigserial primary key,
    username varchar unique,
    color    varchar,
    created  timestamp without time zone
);

create table green_chat.chats
(
    id       bigserial primary key,
    users    bigint[] not null,
    name     varchar,
    is_group boolean not null default false,
    created  timestamp without time zone not null default current_timestamp
);

create table green_chat.messages
(
    id      bigserial primary key,
    chat_id bigint references green_chat.chats (id),
    user_id bigint references green_chat.users (id),
    message varchar,
    created timestamp without time zone not null default current_timestamp
);