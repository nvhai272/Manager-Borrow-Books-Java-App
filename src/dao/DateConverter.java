/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    public static String DateFormat(String inputDateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); 
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy"); 
        try {
            Date date = inputFormat.parse(inputDateString); 
            return outputFormat.format(date); 
        } catch (ParseException e) {
            e.printStackTrace(); 
            return null; 
        }
    }    
     public static String DateFormat2(String inputDateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy"); 
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy"); 
        try {
            Date date = inputFormat.parse(inputDateString); 
            return outputFormat.format(date); 
        } catch (ParseException e) {
            e.printStackTrace(); 
            return null; 
        }
    }    
}
