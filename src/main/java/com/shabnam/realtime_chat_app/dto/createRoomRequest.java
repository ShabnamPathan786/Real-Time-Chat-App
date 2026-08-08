package com.shabnam.realtime_chat_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class createRoomRequest{
    @NotBlank(message="roomid should not be blank")
    private String roomId;

    @NotBlank(message="createdBy should not be blank")
    private String createdBy;
}

