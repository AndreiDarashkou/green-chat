package org.green.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String fromUserId; //who send
    private String toUserId;   //who should receive
    private String message;
    private Instant timestamp;
}
