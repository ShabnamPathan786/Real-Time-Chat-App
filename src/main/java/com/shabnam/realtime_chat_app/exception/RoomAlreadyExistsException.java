package com.shabnam.realtime_chat_app.exception;

public class RoomAlreadyExistsException extends RuntimeException{
       public RoomAlreadyExistsException(String message){
        super(message);
       }
}
