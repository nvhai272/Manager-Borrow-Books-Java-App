/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

/**
 *
 * @author hainv
 */
@Getter
@Setter
public class User {
    public static String COLLECTION = "User";
    private ObjectId id;
    
    @BsonProperty("name")
    private String name;
    
    @BsonProperty("phone_no")
    private String phoneNo;
    
    @BsonProperty("password")
    private String password;
    
    @BsonProperty("email")
    private String email;
    
    @BsonProperty("dob")
    private Date dob;
    
    @BsonProperty("type")
    private String type;
    
    @BsonProperty("quantity_borrowing")
    private String quantityBorrowing;
    
    @Override
    public String toString() {
        return name;
    }
}
