/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import model.User;
import util.DBUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
/**
 *
 * @author hainv
 */
public class AuthenticationDAO {
    public User login(String phoneNo, String pass) {
     String passValue = this.maHoaPasswordMD5(pass);
     try ( MongoClient client = DBUtils.open()) {
        Bson phoneFilter = Filters.eq("phone_no",phoneNo);
        Bson passFilter = Filters.eq("password",passValue);
        Bson loginFilter = Filters.and(phoneFilter,passFilter);
        // check lại phone/pass xem có exception khong
        // có thì try...catch và return null trong catch
        return DBUtils.getDatabase(client)
                    .getCollection(User.COLLECTION, User.class)
                    .find(loginFilter)
                .first();
        }
   
    }

    private String maHoaPasswordMD5(String pass) {
        return  pass;
    }
             
            
}
