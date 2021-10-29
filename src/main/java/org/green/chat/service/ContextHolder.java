package org.green.chat.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContextHolder<T> {

    private T data;
}
