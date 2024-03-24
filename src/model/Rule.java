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
public class Rule {

    public static String COLLECTION = "Rule";
    private ObjectId id;
    
    @BsonProperty("type")
    private String type;
    
    @BsonProperty("fine")
    private float fine;
    
    @BsonProperty("max_fine")
    private float maxFine;
    
}
