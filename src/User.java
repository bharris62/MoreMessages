import java.util.ArrayList;

/**
 * Created by BHarris on 2/21/17.
 */
public class User {
    String name;
    String passWord;
    ArrayList<Message> messages = new ArrayList<>();

    public User(String name, String passWord) {
        this.name = name;
        this.passWord = passWord;
    }
}
