package com.sam.jcc.cloud.i;

/**
 * @author Alexey Zhytnik
 * @since 17.11.2016
 */
public class BusinessCloudException extends CloudException {

    public BusinessCloudException(){
        super();
    }

    public BusinessCloudException(String message){
        super(message);
    }
}
