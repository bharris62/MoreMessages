import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;


public class Main {
    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        Spark.init();
        Spark.get("/",
                (request, response) -> {
                    HashMap m = new HashMap();
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);

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
            if(users.containsKey(name)){
                if(! users.get(name).passWord.equals(password)){
                    response.redirect("/");
                    return "";
                }
            }
            users.putIfAbsent(name, new User(name, password));
            Session session = request.session();
            session.attribute("userName", name);
            response.redirect("/");
            return "";
        });

        Spark.post("/messages", (request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            if (user == null) {
                throw new Exception("User is not logged in");
            }

            String text = request.queryParams("message");
            Message message = new Message(text);
            user.messages.add(message);
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
            User user = users.get(name);
            user.messages.remove(num -1);
            response.redirect("/");

            return "";
        });

        Spark.post("/edit", (request, response)->{
            int num = Integer.parseInt(request.queryParams("toEdit"));
            String text = request.queryParams("editText");
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            Message newMessage = new Message(text);
            user.messages.set(num - 1, newMessage );
            response.redirect("/");
            return "";
        });
    }
}


