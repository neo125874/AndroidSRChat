package chat.signalr.android.com.androidsrchat;

public class User{
    int image;
    String name;
    int msgCnt;

    public User(int image, String name, int msgCnt){
        super();
        this.image = image;
        this.name = name;
        this.msgCnt = msgCnt;
    }

    public int getMsgCnt() {
        return msgCnt;
    }

    public void setMsgCnt(int msgCnt) {
        this.msgCnt = msgCnt;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
