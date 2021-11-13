package org.green.chat.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

@Data
@Table("chats")
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    private Long id;
    private List<Long> users;
    private String name;
    private boolean group;
    private Instant created;
}
