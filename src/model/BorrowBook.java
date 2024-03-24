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
public class BorrowBook {

    public static String COLLECTION = "BorrowBook";

    private ObjectId id;
    @BsonProperty("borrowed_date")
    private Date borrowedDate;
    @BsonProperty("duration_borrow")
    private Date durationBorrow;
    @BsonProperty("return_date")
    private Date returnDate;
    @BsonProperty("duration")
    private int duration;
    @BsonProperty("fine_amount")
    private float fineAmount;
    @BsonProperty("fine_reason")
    private String fineReason;
    @BsonProperty("borrow_status")
    private String borrowStatus;
    @BsonProperty("book_id")
    private ObjectId bookId;
    private Book book;
    @BsonProperty("user_id")
    private ObjectId userId;
    private User user;
    @BsonProperty("admin_id")
    private ObjectId adminId;
    private Admin admin;

    public void setBookId(ObjectId bookId) {
        this.bookId = bookId;
    }
}
