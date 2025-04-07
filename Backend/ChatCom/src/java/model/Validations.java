
package model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class Validations {

    public static boolean isPasswordInvalid(String password) {
        return !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    }
    
    public static boolean isMobileInvalid(String mobile){
        return !mobile.matches("07[01245678]{1}[0-9]{7}");
    }

}
