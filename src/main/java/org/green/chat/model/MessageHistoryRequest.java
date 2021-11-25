package org.green.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageHistoryRequest {
    private Long chatId;
    private Instant from = Instant.now();
    private int limit = 10;
}
