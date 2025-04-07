
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User implements  Serializable{

    public User() {
    }
    
   @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int id;
    
    @Column(name = "mobile",length = 10,nullable = false)
     private String mobile;
   
    @Column(name = "first_name",length = 45,nullable = false)
     private String firstname;
    
    @Column(name = "last_name",length = 45,nullable = false)
     private String lastname;
      
    
    @Column(name = "password",length = 20,nullable = false)
     private String password;
    
    @Column(name = "registered_datetime",nullable = false)
     private Date regdatetime;
    
   @ManyToOne
    @JoinColumn(name = "user_status_id")
      private UserStatus userStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegdatetime() {
        return regdatetime;
    }

    public void setRegdatetime(Date regdatetime) {
        this.regdatetime = regdatetime;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }


    
}
