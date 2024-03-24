/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

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
public class Admin {

    public static String COLLECTION = "Admin";
    private ObjectId id;
    
    @BsonProperty("name")
    private String name;
    
    @BsonProperty("phone_no")
    private String phoneNo;
    
    @BsonProperty("password")
    private String password;
    
    @BsonProperty("email")
    private String email;

    @Override
    public String toString() {
        return name;
    }
}
