package com.shabnam.realtimechat.exception;

public class RoomAlreadyExistsException extends RuntimeException{
    public RoomAlreadyExistsException(String message){
        super(message);
    }
}
