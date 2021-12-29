alter table green_chat.messages add column number int;

with ordered as (
    select id, chat_id, row_number() over(partition by chat_id order by created) as num
    from green_chat.messages)
update green_chat.messages as m
set number = o.num
from ordered as o
where o.id = m.id;


create or replace function green_chat.update_message_count()
    returns trigger as
$$
declare
    prev_number int;
begin
    select number into prev_number from green_chat.messages where chat_id = NEW.chat_id order by id desc limit 1;
    if prev_number is null then
        prev_number = 0;
    end if;
    NEW.number := prev_number + 1;
    return NEW;
end
$$ language plpgsql;

create trigger update_message_count
    before insert
    on green_chat.messages
    for each row
execute procedure green_chat.update_message_count();