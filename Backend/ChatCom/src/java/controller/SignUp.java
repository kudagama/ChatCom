package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.UserStatus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "signUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        // JsonObject requestJson =gson.fromJson(request.getReader(),JsonObject.class);
        String mobile = request.getParameter("mobile");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String password = request.getParameter("password");
        Part avatarImage = request.getPart("avatarImage");
   
        
        if (mobile.isEmpty()) {
            responseJson.addProperty("message", "Enter Mobile");
      } 
       else if (mobile.isEmpty()) {
            responseJson.addProperty("message", "Enter Mobile");
//       } else if (!Validations.isMobileInvalid(mobile)) {
//           responseJson.addProperty("message", "Invalid Mobile");
        } else if (firstName.isEmpty()) {
            responseJson.addProperty("message", "Enter First Name");
        } else if (lastName.isEmpty()) {
            responseJson.addProperty("message", "Enter Last Name");
        } else if (password.isEmpty()) {
            responseJson.addProperty("message", "Enter Password");
        } else if (!Validations.isPasswordInvalid(password)) {
            responseJson.addProperty("message", "Invalid Password");
        
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
                        Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("mobile", mobile));

            if (!criteria1.list().isEmpty()) {
                responseJson.addProperty("message", "Already Registered");
            } else {

            User user = new User();
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setMobile(mobile);
            user.setPassword(password);
            user.setRegdatetime(new Date());

//get user status 2 -offline     
            UserStatus userStatus = (UserStatus) session.get(UserStatus.class, 2);
            user.setUserStatus(userStatus);

            session.save(user);
            session.beginTransaction().commit();

            if (avatarImage != null) {
                String serverPath = request.getServletContext().getRealPath("");
                System.out.println(serverPath);
                String avatarImagePath = serverPath + File.separator + "AvatarImages" + File.separator + mobile + ".png";
                File file = new File(avatarImagePath);
                Files.copy(avatarImage.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Registration Completed");
            }
            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));

    }

}
