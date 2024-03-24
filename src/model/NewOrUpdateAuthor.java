/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author hainv
 */
@Getter
@Setter
public class NewOrUpdateAuthor {

    private String name;
    private Date dob;
    private int quantityBookOfAuthor;
    private String email;
    private String phoneNo;

}
