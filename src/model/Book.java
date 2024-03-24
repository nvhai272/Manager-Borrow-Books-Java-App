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
public class Book {

//    public BookInfo(String string, String string1, String string2) {
//    }

    public static String COLLECTION = "Book";
    private ObjectId id;
    
    @BsonProperty("isbn")
    private String isbn;
    
    @BsonProperty("book_title")
    private String bookTitle;
    
    @BsonProperty("price")
    private float price;
    
    @BsonProperty("quantity")
    private int quantity;
    
    @BsonProperty("remain")
    private int remain;
    
    @BsonProperty("created_at")
    private Date createAt;
    
    @BsonProperty("updated_at")
    private Date updateAt;
    
    @BsonProperty("status")
    private String status;
    
    @BsonProperty("author_id")
    private ObjectId authorId;
    private Author author;
    
//    @BsonProperty("admin_id")
//    private ObjectId adminId;
//    private Admin admin;
    
    @Override
    public String toString() {
        return bookTitle;
    }

}
