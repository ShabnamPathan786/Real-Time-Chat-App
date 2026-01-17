package com.shabnam.realtimechat.request_response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String content;
    private String sender;
}
