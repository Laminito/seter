package sn.setter.generic;


import org.springframework.stereotype.Component;

@Component
public class ResponseFactory {
    public static CustomResponse seterResponse(String status, int statusCode, String message, Object data) {
        return new CustomResponse(status, statusCode, message, data);
    }
}