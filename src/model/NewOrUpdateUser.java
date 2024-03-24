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
public class NewOrUpdateUser {

    private String name;
    private String phoneNo;
    private String password;
    private String email;
    private Date dob;
    private int quantityBorrowing;

}
