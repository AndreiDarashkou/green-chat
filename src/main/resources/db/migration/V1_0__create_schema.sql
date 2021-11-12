drop schema if exists green_chat cascade;

create schema green_chat;

create table green_chat.uses
(
    id      bigserial primary key,
    name varchar,
    color varchar,
    created timestamp without time zone
);

create table green_chat.messages
(
    id      bigserial primary key,
    chat_id bigint,
    user_id bigint,
    message varchar,
    created timestamp without time zone
);

create table green_chat.chats
(
    id      bigserial primary key,
    users   bigint[] not null default array[]::bigint[],
    name    varchar not null,
    is_group   boolean not null,
    created timestamp without time zone not null default current_timestamp
);