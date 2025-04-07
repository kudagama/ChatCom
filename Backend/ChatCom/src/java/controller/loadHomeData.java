package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.User;
import entity.UserStatus;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
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

@WebServlet(name = "loadHomeData", urlPatterns = {"/loadHomeData"})
public class loadHomeData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         
        Gson gson = new Gson();
        JsonObject respoJson = new JsonObject();
        respoJson.addProperty("success", false);
        respoJson.addProperty("message", "unable to proces ur req");

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            //get user id req paramter
            String userId = req.getParameter("id");

            //get user obj
            User user = (User) session.get(User.class, Integer.parseInt(userId));

            //get suer online
            UserStatus userStatus = (UserStatus) session.get(UserStatus.class, 1);

            //update user starus
            user.setUserStatus(userStatus);
            session.update(user);

            //get other users
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.ne("id", user.getId()));

            List<User> otherUserList = criteria1.list();

            JsonArray jsonChatArray = new JsonArray();
            for (User otherUser : otherUserList) {

                //get chats
                Criteria criteria2 = session.createCriteria(Chat.class);
                criteria2.add(
                        Restrictions.or(
                                Restrictions.and(
                                        Restrictions.eq("fromuser", user),
                                        Restrictions.eq("touser", otherUser)
                                ),
                                Restrictions.and(
                                        Restrictions.eq("fromuser", otherUser),
                                        Restrictions.eq("touser", user)
                                )
                        )
                );

                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                ///cretr chat itrem json tp  sent frontend data
                JsonObject jsonChatItem = new JsonObject();
                jsonChatItem.addProperty("other_user_id", otherUser.getId());
                jsonChatItem.addProperty("other_user_mobile", otherUser.getMobile());
                jsonChatItem.addProperty("other_user_name", otherUser.getFirstname() + " " + otherUser.getLastname());
                jsonChatItem.addProperty("other_user_status", otherUser.getUserStatus().getId()); // 1  = online 2 = offline

         //check avatar img
           String serverPath = req.getServletContext().getRealPath("");
            String  otherUserAvatarImagePath =serverPath + File.separator+"AvatarImages"+File.separator+ otherUser.getMobile()+ ".png";
            File otherUserAvatarImageFile = new File(otherUserAvatarImagePath);
                
                  if (otherUserAvatarImageFile.exists()) {
                //avatr img found
                jsonChatItem.addProperty("avatar_image_found", true);
            } else {
                jsonChatItem.addProperty("avatar_image_found", false);
                jsonChatItem.addProperty("other_user_avatar_letters", otherUser.getFirstname().charAt(0)+" "+otherUser.getLastname().charAt(0));
            } 
                
                
                
                //get chat list
                List<Chat> dbChatList = criteria2.list();
                SimpleDateFormat dateFormat = new SimpleDateFormat(" MMM dd, HH:mm ");

                if (dbChatList.isEmpty()) {
                    //no chat
                    jsonChatItem.addProperty("message", "Say Hi " +otherUser.getFirstname());
                                 
                    jsonChatItem.addProperty("dateTime", dateFormat.format(user.getRegdatetime()));
                    jsonChatItem.addProperty("chat_status_id", 1);  //1 =seen  2 = unseen 
                } else {
                    //found last chat 
                    jsonChatItem.addProperty("message", dbChatList.get(0).getMessage());
                    jsonChatItem.addProperty("dateTime", dateFormat.format(dbChatList.get(0).getDatetime()));
                    jsonChatItem.addProperty("chat_status_id", dbChatList.get(0).getChatStatus().getId());  //1 =seen  2 = unseen 
                }
//get last conversation
jsonChatArray.add(jsonChatItem); 
            }

            //send users
            respoJson.addProperty("success", true);
            respoJson.addProperty("message", "success");
         // respoJson.add("user", gson.toJsonTree(user));
            respoJson.add("jsonChatArray", gson.toJsonTree(jsonChatArray));

            session.beginTransaction().commit();
            session.close();

        } catch (Exception e) {
               
        }

          resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(respoJson));
    }

}
