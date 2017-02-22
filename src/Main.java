import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;


public class Main {
    static User user;
    static ArrayList<Message> messages = new ArrayList<>();

    public static void main(String[] args) {
        Spark.init();
        Spark.get("/",
                (request, response) -> {
                    HashMap<String, Object> m = new HashMap();
                    if(user == null){
                        return new ModelAndView(m, "index.html");
                    }else{
                        m.put("name", user.name);
                        m.put("message", messages);
                        return new ModelAndView(m, "messages.html");
                    }
                },
                new MustacheTemplateEngine());

        Spark.post("/index", (request, response) -> {
            String name = request.queryParams("loginName");
            name = name.substring(0,1).toUpperCase() + name.substring(1);
            user = new User(name);
            response.redirect("/");
            return "";
        });

        Spark.post("/messages", (request, response) -> {
            String text = request.queryParams("message");
            Message message = new Message(text);
            messages.add(message);
            response.redirect("/");
            return "";
        });
    }
}


