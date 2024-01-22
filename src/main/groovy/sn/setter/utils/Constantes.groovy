package sn.setter.utils

class Constantes {
    public static final int BCRYPT_STRENGTH = 12
    public static final String SECRET_KEY = "cTwnuz5Y6DIiljX7XQPg6LwBdOFFlfzhl4DXnZsbYStIOfS13zJ4idFGl9IjtEyTAobSQbfquREbatYhXGs0qN4yaccBRbzIm6ONUmETODf2qChQbQHLQlwArySu7yYhQE0y2n1iIgKeIvyPp3jr6Tl3i134wbmFITgVMCldid6fH9Q7VU2sYBMJoryMv9w3xdXAobc1Qr2j7qkyGKBaQTfOTFXDK09UL2fCPIhS5fgjHhjLvMxIM8Zec1NscTBY7gmogH6fxIccqVbVUTVwfQfzBhy9pNVcXoQKHAYt8vRS\n"

    // Status Constants
    public static class Status {
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int CONFLICT = 409;
    }

    // Message Constants
    public static class Message {
        public static final String SUCCESS_BODY = "Success";
        public static final String USER_CREATED_BODY = "Created successfully";
        public static final String BAD_REQUEST_BODY = "Invalid input data";
        public static final String UNAUTHORIZED_BODY = "Unauthorized";
        public static final String NOT_FOUND_BODY = "Not Found";
        public static final String SERVER_ERROR_BODY = "Internal server error";
        public static final String CONFLICT_BODY = "Conflict";
    }
}
