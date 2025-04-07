package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "signIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("Success", false);

        JsonObject requstJson = gson.fromJson(req.getReader(), JsonObject.class);
        String mobile = requstJson.get("mobile").getAsString();
        String password = requstJson.get("password").getAsString();

        if (mobile.isEmpty()) {
            responseJson.addProperty("message", "Enter Mobile");
//        } else if (!Validations.isMobileInvalid(mobile)) {
//            responseJson.addProperty("message", "Invalid Mobile");
        } else if (password.isEmpty()) {
            responseJson.addProperty("message", "Enter Password");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();

            
            //search number & pssword
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("mobile", mobile));
            criteria1.add(Restrictions.eq("password", password));

            if (!criteria1.list().isEmpty()) {
// user found
                User user = (User) criteria1.uniqueResult();

                    responseJson.addProperty("success", true);
                responseJson.addProperty("message", "sign success");
               responseJson.add("user", gson.toJsonTree(user));

            } else {
                //user 404
                responseJson.addProperty("message", "invaild creadintials");
            }
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
//        
//        responseJson.addProperty("message", "Server:Hello!");

    }

}
