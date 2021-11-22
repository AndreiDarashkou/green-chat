package org.green.chat.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.green.chat.model.CreateChatRequest;
import org.green.chat.util.ColorUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

@Data
@Table("chats")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    private Long id;
    private List<Long> users;
    private String name;
    @Column("is_group")
    private boolean group;
    private String color;
    private Instant created;

    public static Chat of(CreateChatRequest request) {
        return Chat.builder()
                .users(request.getUsers())
                .name(request.getName())
                .color(request.isGroup() ? ColorUtils.randomColor() : null)
                .group(request.isGroup())
                .created(Instant.now())
                .build();
    }
}
