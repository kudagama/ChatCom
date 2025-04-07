package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.ChatStatus;
import entity.User;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadChat", urlPatterns = {"/LoadChat"})
public class LoadChat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//
        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        String logged_user_id = req.getParameter("logged_user_id");
        String other_user_id = req.getParameter("other_user_id");

//get logged iser
        User logged_user = (User) session.get(User.class, Integer.parseInt(logged_user_id));

//get other iser
        User other_user = (User) session.get(User.class, Integer.parseInt(other_user_id));

//get chat
        Criteria criteria1 = session.createCriteria(Chat.class);
        criteria1.add(
                Restrictions.or(
                        Restrictions.and(
                                Restrictions.eq("fromuser", logged_user),
                                Restrictions.eq("touser", other_user)),
                        Restrictions.and(
                                Restrictions.eq("fromuser", other_user),
                                Restrictions.eq("touser", logged_user))
                )
        );

        //sort chts
        criteria1.addOrder(Order.asc("datetime"));

        //get chatlist
        List<Chat> chat_list = criteria1.list();

        //get cht status= 1 (seen)
        ChatStatus chatStatus = (ChatStatus) session.get(ChatStatus.class, 1);

        //cht arry
        JsonArray chatArray = new JsonArray();

     //create date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm ");

        for (Chat chat : chat_list) {
           JsonObject chatObject = new JsonObject();
            chatObject.addProperty("message", chat.getMessage());
            chatObject.addProperty("datetime", dateFormat.format(chat.getDatetime()));

            //get cht only from other user
            if (chat.getFromuser().getId() == other_user.getId()) {

                //add side to cht obj
                chatObject.addProperty("side", "left");

                //un seen chts
                if (chat.getChatStatus().getId() == 2) {
                    //updtre chts
                    chat.setChatStatus(chatStatus);
                    session.update(chat);
                }
            } else {
                //get cht gorm logged user
            
                //add side to vht obj
                chatObject.addProperty("side", "right");
                chatObject.addProperty("status", chat.getChatStatus().getId());  //1= seen  2= unseen
            }

            chatArray.add(chatObject);
        }

        //updte dbb
        session.beginTransaction().commit();

        //send responce
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(chatArray));

    }
}
