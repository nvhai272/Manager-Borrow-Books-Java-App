/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

import com.mongodb.MongoException;

/**
 *
 * @author hainv
 */
public class ApplicationException extends Exception{
    public ApplicationException(String msg){
        super(msg);
    }
}
