package org.green.chat.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table("messages")
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id;
    private long chatId;
    private long userId;
    private String message;
    private Instant timestamp;
}
