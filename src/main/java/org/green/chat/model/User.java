package org.green.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.green.chat.util.ColorUtils;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public static final User EMPTY = User.of("Чебуашка");

    private String id;
    private String username;
    private String color;

    public static User of(String username) {
        return new User(UUID.randomUUID().toString(), username, ColorUtils.randomColor());
    }
}
