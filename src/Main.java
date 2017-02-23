import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static class UserControl {
        static HashMap<String, User> users = new HashMap<>();

        public UserControl(){}

        public static HashMap<String, User> getUsers() {
            return users;
        }

        public static void setUsers(HashMap<String, User> users) {
            UserControl.users = users;
        }
    }

    static UserControl userControl = new UserControl();
    public static void main(String[] args) {
        Spark.init();
        Spark.get("/",
                (request, response) -> {
                    HashMap m = new HashMap();
                    loadMessages();
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = userControl.users.get(name);
                    if(user == null){
                        return new ModelAndView(m, "login.html");
                    }else{
                        return new ModelAndView(user, "messages.html");
                    }
                },
                new MustacheTemplateEngine());

        Spark.post("/login", (request, response) -> {
            String name = request.queryParams("loginName");
            String password = request.queryParams("passWord");
            name = name.substring(0,1).toUpperCase() + name.substring(1);
            Session session = request.session();
            if(UserControl.users.containsKey(name)){
                if(! UserControl.users.get(name).passWord.equals(password)){
                    response.redirect("/");
                    return "";
                }
            }
            UserControl.users.putIfAbsent(name, new User(name, password));

            session.attribute("userName", name);
            saveFile();
            response.redirect("/");
            return "";
        });

        Spark.post("/messages", (request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            User user = UserControl.users.get(name);
            if (user == null) {
                throw new Exception("User is not logged in");
            }

            String text = request.queryParams("message");
            Message message = new Message(text);
            user.messages.add(message);
            saveFile();
            response.redirect("/");
            return "";
        });

        Spark.post("/logout", (request, response) -> {
            Session session = request.session();
            session.invalidate();
            response.redirect("/");
            return "";
        });

        Spark.post("/delete", (request, response) -> {
            int num = Integer.parseInt(request.queryParams("toDelete"));
            Session session = request.session();
            String name = session.attribute("userName");
            User user = UserControl.users.get(name);
            user.messages.remove(num -1);
            saveFile();
            response.redirect("/");

            return "";
        });

        Spark.post("/edit", (request, response)->{
            int num = Integer.parseInt(request.queryParams("toEdit"));
            String text = request.queryParams("editText");
            Session session = request.session();
            String name = session.attribute("userName");
            User user = UserControl.users.get(name);
            Message newMessage = new Message(text);
            user.messages.set(num - 1, newMessage );
            saveFile();
            response.redirect("/");
            return "";
        });
    }

    private static void loadMessages() throws FileNotFoundException {
        File f = new File("messages.json");
        Scanner scanner = new Scanner(f);
        String contents = scanner.nextLine();
        scanner.close();
        JsonParser p = new JsonParser();
        userControl =  p.parse(contents, UserControl.class);
    }

    public static void saveFile() throws IOException {
        JsonSerializer serializer = new JsonSerializer();
        String json = serializer
                .include("*")
                .serialize(UserControl.users);

        File f = new File("messages.json");
        FileWriter fw = new FileWriter(f);

        fw.write(json);
        fw.close();
    }

    static boolean checkIfEmpty(String text){
        if(text == null){
            return false;
        }else return true;
    }
}


