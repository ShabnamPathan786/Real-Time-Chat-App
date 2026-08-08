package com.shabnam.realtime_chat_app.exception;

public class RoomNotFoundException extends RuntimeException{
       public  RoomNotFoundException(String message){
        super(message);
       }
}
