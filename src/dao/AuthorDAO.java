/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import exception.ApplicationException;
import model.Author;
import model.NewOrUpdateAuthor;
import util.DBUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 *
 * @author hainv
 */
public class AuthorDAO {

    public Author findAuthorById(ObjectId authorId) {
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase db = DBUtils.getDatabase(client);
            MongoCollection<Author> collection = db.getCollection(Author.COLLECTION, Author.class);
            return collection.find(Filters.eq("_id", authorId))
                    .first();
        }
    }

    public Map<ObjectId, Author> findAuthorByIds(MongoClient client, Set<ObjectId> ids) {
        Map<ObjectId, Author> map = new HashMap<>();
        MongoDatabase db = DBUtils.getDatabase(client);
        MongoCollection<Author> collection = db.getCollection(Author.COLLECTION, Author.class);
        Set<ObjectId> idObjs = ids.stream()
                .collect(Collectors.toSet());
        Bson filter = Filters.in("_id", idObjs);
        collection.find(filter)
                .forEach(it -> map.put(it.getId(), it));
        return map;
    }

    public List<Author> findAllAuthors() {
        List<Author> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(Author.COLLECTION, Author.class)
                    .find()
                    .forEach(results::add);
//                  .forEach(it-> results.add(it));
        }
        return results;
    }

    public boolean addNewAuthor(NewOrUpdateAuthor newAuthor) {
        Author aut = new Author();
        aut.setName(newAuthor.getName());
        aut.setDob(newAuthor.getDob());
        aut.setEmail(newAuthor.getEmail());
        aut.setPhoneNo(newAuthor.getPhoneNo());
        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(Author.COLLECTION, Author.class)
                    .insertOne(aut);
        }
        return true;
    }

    public boolean updateAuthor(ObjectId id, NewOrUpdateAuthor newAuthor) throws ApplicationException {
        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.combine(
                Updates.set("name", newAuthor.getName()),
                Updates.set("email", newAuthor.getEmail()),
                Updates.set("phone_no", newAuthor.getPhoneNo()),
                Updates.set("dob", newAuthor.getDob())
        );

        try (MongoClient client = DBUtils.open()) {
            UpdateResult updateResult = DBUtils.getDatabase(client)
                    .getCollection(Author.COLLECTION, Author.class)
                    .updateOne(filter, update);
            if (updateResult.getModifiedCount() > 0) {
                return true;
            } else {
                throw new ApplicationException("Không tìm thấy hoặc không cập nhật được sách");
            }
        }
    }
// kiểm tra xem tác giả có sách hay k????

    public boolean authorHasBooks(ObjectId authorId) {
        try (MongoClient client = DBUtils.open()) {
            MongoCollection<org.bson.Document> booksCollection = DBUtils.getDatabase(client)
                    .getCollection("Book");
            // Tìm sách dựa trên authorId
            BasicDBObject query = new BasicDBObject("author_id", authorId);
            FindIterable<org.bson.Document> iterable = booksCollection.find(query);
            // Kiểm tra xem có sách nào liên kết với tác giả không
            return iterable.iterator().hasNext();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAuthor(ObjectId id) throws ApplicationException {
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);

            MongoCollection<org.bson.Document> booksCollection = database.getCollection("Book");
            long bookCount = booksCollection.countDocuments(Filters.eq("author_id", id));

            if (bookCount > 0) {
                String msg = String.format("Không thể xóa %s vì tác giả có sách liên kết.", id.toString());
                throw new ApplicationException(msg);
            } else {
                MongoCollection<org.bson.Document> authorsCollection = database.getCollection("Author");
                authorsCollection.deleteOne(Filters.eq("_id", id));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //đếm số sách của tác giả
    public long countBookOfAuthor(ObjectId authorId) {
        MongoClient client = DBUtils.open();
        MongoDatabase database = DBUtils.getDatabase(client);
        MongoCollection<Document> collection = database.getCollection("Book");
        Bson filter = Filters.eq("author_id", authorId);
        long count = collection.countDocuments(filter);
        return count;
    }

    public Author findAuthorByPhoneNoAndEmail(String phoneNo, String mail) {
        try (MongoClient client = DBUtils.open()) {
            return DBUtils.getDatabase(client)
                    .getCollection(Author.COLLECTION, Author.class)
                    .find(Filters.and(Filters.eq("phone_no", phoneNo), Filters.eq("email", mail)))
                    .first();
        }
    }
    
    public Author findAuthorByPhoneNo(String phoneNo) {
        try (MongoClient client = DBUtils.open()) {
            return DBUtils.getDatabase(client)
                    .getCollection(Author.COLLECTION, Author.class)
                    .find(Filters.eq("phone_no", phoneNo))
                    .first();
        }
    }
    
    public Author findAuthorByEmail(String email) {
        try (MongoClient client = DBUtils.open()) {
            return DBUtils.getDatabase(client)
                    .getCollection(Author.COLLECTION, Author.class)
                    .find(Filters.eq("email", email))
                    .first();
        }
    }
}
