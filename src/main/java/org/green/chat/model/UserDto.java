package org.green.chat.model;

import java.time.Instant;

public record UserDto(long id, String username, String color, Instant created) {
}
