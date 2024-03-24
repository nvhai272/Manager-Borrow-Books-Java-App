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
public class Author{

    public static String COLLECTION = "Author";
    private ObjectId id;

    @BsonProperty("name")
    private String name;

    @BsonProperty("dob")
    private Date dob;

    @BsonProperty("quantity_book")
    private int quantityBookOfAuthor;

    @BsonProperty("email")
    private String email;

    @BsonProperty("phone_no")
    private String phoneNo;

    @Override
    public String toString() {
        return name;
    }

}
