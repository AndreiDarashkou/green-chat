package org.green.chat.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table("users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    private Long id;
    private String username;
    private String color;
    private Instant created;

    public static UserEntity of(String username) {
        return new UserEntity(null, username, null, null);
    }
}
