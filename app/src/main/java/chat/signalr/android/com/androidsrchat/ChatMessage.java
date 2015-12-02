package chat.signalr.android.com.androidsrchat;

/**
 * Created by tw4585 on 2015/12/1.
 */
public class ChatMessage {
    //private long id;
    private String withName;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;

    public ChatMessage() {
    }

    public ChatMessage(String withName, boolean isMe, String message, String dateTime) {
        this.withName = withName;
        this.isMe = isMe;
        this.message = message;
        this.dateTime = dateTime;
    }

    public String getWithName() {
        return withName;
    }
    public void setWithName(String withName) {
        this.withName = withName;
    }
    /*public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }*/
    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}
