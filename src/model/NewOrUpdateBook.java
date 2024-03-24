/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

/**
 *
 * @author hainv
 */
@Getter
@Setter
public class NewOrUpdateBook {
    
    private String isbn;
    private String bookTitle;
    private float price;
    private int quantity;
    private int remain;
    private String status;
   // chỗ này nếu update goi den thang duoi sẽ insert null vì no k tim thay 
//    private ObjectId authorId;
//    private ObjectId adminId;

}
