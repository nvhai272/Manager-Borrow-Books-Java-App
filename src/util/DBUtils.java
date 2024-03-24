/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 *
 * @author hainv
 */
public class DBUtils {
   public static MongoClient open() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    public static MongoDatabase getDatabase(MongoClient client) {
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider
                        .builder().automatic(true).build())
        );
        return client.getDatabase("manager_borrow_book")
                .withCodecRegistry(codecRegistry);

    } 
}
