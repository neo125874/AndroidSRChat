package chat.signalr.android.com.androidsrchat;

public class User{
    int image;
    String name;

    public User(int image, String name){
        super();
        this.image = image;
        this.name = name;
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
