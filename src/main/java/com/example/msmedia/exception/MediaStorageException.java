package com.example.msmedia.exception;

import com.example.libexception.exception.InternalServerErrorException;
import com.example.msmedia.exception.error.DocumentErrorCode;

public class MediaStorageException extends InternalServerErrorException {

    public MediaStorageException(String message) {
        super(message);
    }
}
