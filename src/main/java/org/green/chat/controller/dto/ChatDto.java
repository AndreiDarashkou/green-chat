package org.green.chat.controller.dto;

import org.green.chat.repository.entity.Message;

import java.time.Instant;
import java.util.List;

public record ChatDto(
        Long id,
        List<Long> users,
        String name,
        boolean group,
        String color,
        Instant created,
        Message lastMessage) {
}
