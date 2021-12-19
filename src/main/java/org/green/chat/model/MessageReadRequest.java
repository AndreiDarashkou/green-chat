package org.green.chat.model;

public record MessageReadRequest(int chatId, int fromId, int toId) {
}
