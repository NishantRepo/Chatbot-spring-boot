package com.nish.demo.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    private String sender;
    private String content;
    private LocalDateTime timestamp;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}