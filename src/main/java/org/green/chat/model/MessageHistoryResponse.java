package org.green.chat.model;

import org.green.chat.repository.entity.Message;

import java.util.List;

public record MessageHistoryResponse(long total, List<Message> list) {
}
