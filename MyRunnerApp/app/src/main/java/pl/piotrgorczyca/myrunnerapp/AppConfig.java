package pl.piotrgorczyca.myrunnerapp;

public class AppConfig {
	//Server machine IP adress
    public static String MACHINE_IP_ADRESS = "http://0000.0000.0000.0000";
    // Server user login url
    public static String URL_LOGIN = MACHINE_IP_ADRESS + "/api/login";

    // Server user register url
    public static String URL_REGISTER = MACHINE_IP_ADRESS + "/api/register";

    // Server training create url
    public static String URL_CREATE_TRAINING = MACHINE_IP_ADRESS + "/api/trainings";

    // Server get trainings
    public static String URL_GET_TRAININGS = MACHINE_IP_ADRESS + "/api/trainings";

    // Server get messages from users
    public static String URL_GET_MESSAGES = MACHINE_IP_ADRESS + "/api/messages";

    // Server get messages from user by id
    public static String URL_GET_MESSAGES_FROM_USER = MACHINE_IP_ADRESS + "/api/users/messages";

    // Server send message to user
    public static String URL_SEND_MESSAGE_TO_USER = MACHINE_IP_ADRESS + "/api/users/messages/id";

    // Server get profile for a user
    public static String URL_GET_USERS_PROFILE = MACHINE_IP_ADRESS + "/api/users/profiles/id";

    // Server get comments for a user
    public static String URL_GET_USERS_COMMENT = MACHINE_IP_ADRESS + "/api/users/comments/id";

    // Server add a comment
    public static String URL_ADD_COMMENT = MACHINE_IP_ADRESS + "/api/users/comments/";

    // Server get attendence
    public static String URL_GET_ATTENDINGS = MACHINE_IP_ADRESS + "/api/trainings/attendings";

    //server add attendence
    public static String URL_ADD_ATTENDENCE = MACHINE_IP_ADRESS + "/api/trainings/attendings/users/id";

    //server delete attendence
    public static String URL_DELETE_ATTENDENCE = MACHINE_IP_ADRESS + "/api/trainings/attendings/id";

    //server get statistics
    public static String URL_GET_STATISTICS = MACHINE_IP_ADRESS + "/api/users/statistics";

    //search for user
    public static String URL_SEARCH_FOR_USER = MACHINE_IP_ADRESS + "/api/users/name";
}