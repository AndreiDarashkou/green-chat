package org.green.chat.model;

public record MessageReadRequest(long chatId, int fromId, int toId) {
}
