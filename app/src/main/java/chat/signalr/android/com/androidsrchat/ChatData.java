package chat.signalr.android.com.androidsrchat;

/**
 * Created by tw4585 on 2015/11/9.
 */
public class ChatData {
    private String name;
    private String message;

    public ChatData(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }
    public String getMessage() {
        return message;
    }
}
