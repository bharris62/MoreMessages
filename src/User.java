import java.util.ArrayList;

public class User {
    String name;
    String passWord;
    ArrayList<Message> messages = new ArrayList<>();
    boolean failLogin = false;

    public User(String name, String passWord) {
        this.name = name;
        this.passWord = passWord;
    }
}
