package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.ChatStatus;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

@WebServlet(name = "SendChat", urlPatterns = {"/SendChat"})
public class SendChat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        Session session = HibernateUtil.getSessionFactory().openSession();

        String logged_user_id = req.getParameter("logged_user_id");
        String other_user_id = req.getParameter("other_user_id");
        String message = req.getParameter("message");

//get logged iser
        User logged_user = (User) session.get(User.class, Integer.parseInt(logged_user_id));

//get other iser
        User other_user = (User) session.get(User.class, Integer.parseInt(other_user_id));

        Chat chat = new Chat();
        ChatStatus chatStatus = (ChatStatus) session.get(ChatStatus.class, 2);
        chat.setChatStatus(chatStatus);

        chat.setDatetime(new Date());
        chat.setFromuser(logged_user);
        chat.setTouser(other_user);
        chat.setMessage(message);

        session.save(chat);
        try {
            session.beginTransaction().commit();
            responseJson.addProperty("success", true);
        } catch (Exception e) {
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));

    }

}
