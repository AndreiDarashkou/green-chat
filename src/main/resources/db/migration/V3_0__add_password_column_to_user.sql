alter table green_chat.users add column password varchar;

update green_chat.users
    set password = '$2a$10$OuYQeBGqbXJoDLwSnGQ6EeLJhmLn1E4y8BtuTCmH2E.rxom.I.Gde'
    where true;