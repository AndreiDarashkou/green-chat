package org.green.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRequest {
    private Long userId;
    private List<Long> users;
    private String name;
    private boolean group;
}
