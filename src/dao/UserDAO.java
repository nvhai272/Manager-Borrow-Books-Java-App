package dao;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import exception.ApplicationException;
import model.NewOrUpdateUser;
import model.User;
import util.DBUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import model.Book;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import util.Authentication;

public class UserDAO {
    
    public User findUserByEmail(String email) {
        try (MongoClient client = DBUtils.open()) {
            return DBUtils.getDatabase(client)
                    .getCollection(User.COLLECTION, User.class)
                    .find(Filters.eq("email", email))
                    .first();
        }
    }

//    public boolean findUserByEmail(String email) {
//         try (MongoClient client = DBUtils.open()) {
//            FindIterable<User> foundEmail = DBUtils.getDatabase(client)
//                    .getCollection(User.COLLECTION, User.class)
//                    .find(Filters.eq("", email));
//
//            return foundEmail.iterator().hasNext();
//        }
//    }
    
    
// trả về id đối tượng theo số điện thoại
    public ObjectId findIdUserByPhoneNo(String phoneNo) {
        User user = this.findUserByPhoneNo(phoneNo);
        if (user != null) {
            return user.getId();
        }
        return null;
    }
    
// trả về thông tin đối tượng theo số điện thoại

    public User findUserByPhoneNo(String phoneNo) {
        try (MongoClient client = DBUtils.open()) {
            return DBUtils.getDatabase(client)
                    .getCollection(User.COLLECTION, User.class)
                    .find(Filters.eq("phone_no", phoneNo))
                    .first();
        }
    }
// trả vè thông tin đối tượng theo id 

    public User findUserById(ObjectId userId) {
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase db = DBUtils.getDatabase(client);
            MongoCollection<User> collection = db.getCollection(User.COLLECTION, User.class);
            return collection.find(Filters.eq("_id", userId))
                    .first();
        }
    }
    // gọi hàm từ BorrowBookDAO

    public Map<ObjectId, User> findUserByIds(MongoClient client, Set<ObjectId> ids) {
        Map<ObjectId, User> map = new HashMap<>();
        MongoDatabase db = DBUtils.getDatabase(client);
        MongoCollection<User> collection = db.getCollection(User.COLLECTION, User.class);
        Set<ObjectId> idObjs = ids.stream()
                .collect(Collectors.toSet());
        Bson filter = Filters.in("_id", idObjs);
        collection.find(filter)
                .forEach(it -> map.put(it.getId(), it));
        return map;
    }

//    public Map<ObjectId, User> findUserPhone(MongoClient client, Set<ObjectId> phoneNumber) {
//        Map<ObjectId, User> map = new HashMap<>();
//        MongoDatabase db = DBUtil.getDatabase(client);
//        MongoCollection<User> collection = db.getCollection(User.COLLECTION, User.class);
//
//        Bson filter = Filters.in("phone_No", phoneNumber);
//        collection.find(filter)
//                .first();
//        return map;
//    }
    // tìm tất cả user
    public List<User> findAllUsers() {
        List<User> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(User.COLLECTION, User.class)
                    .find()
                    .forEach(results::add);
        }
        return results;
    }
// them mới

    public boolean addNewUser(NewOrUpdateUser newUser) {
        User u = new User();
        u.setName(newUser.getName());
        u.setPhoneNo(newUser.getPhoneNo());
        u.setPassword(newUser.getPassword());
        u.setEmail(newUser.getEmail());
        u.setDob(newUser.getDob());

        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(User.COLLECTION, User.class)
                    .insertOne(u);
        }
        return true;
    }
// cập nhật user thôii

//     public boolean updateUserNoEmailandPhone(ObjectId id, NewOrUpdateUser newUser) throws ApplicationException {
//        Bson filter = Filters.eq("_id", id);
//        Bson update = Updates.combine(
//                Updates.set("name", newUser.getName()),
////                Updates.set("phone_no", newUser.getPhoneNo()),
//                Updates.set("password", Authentication.getMd5Password(newUser.getPassword())),
////                Updates.set("email", newUser.getEmail()),
//                Updates.set("dob", newUser.getDob())
//        );
//        try (MongoClient client = DBUtils.open()) {
//            UpdateResult updateResult = DBUtils.getDatabase(client)
//                    .getCollection(User.COLLECTION, User.class)
//                    .updateOne(filter, update);
//            if (updateResult.getModifiedCount() > 0) {
//                return true;
//            } else {
//                throw new ApplicationException("Không tìm thấy hoặc không cập nhật được bạn đọc");
//            }
//        }
//    }
    
    public boolean updateUser(ObjectId id, NewOrUpdateUser newUser) throws ApplicationException {
        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.combine(
                Updates.set("name", newUser.getName()),
//                Updates.set("phone_no", newUser.getPhoneNo()),
                Updates.set("password", Authentication.getMd5Password(newUser.getPassword())),
//                Updates.set("email", newUser.getEmail()),
                Updates.set("dob", newUser.getDob())
        );
        try (MongoClient client = DBUtils.open()) {
            UpdateResult updateResult = DBUtils.getDatabase(client)
                    .getCollection(User.COLLECTION, User.class)
                    .updateOne(filter, update);
            if (updateResult.getModifiedCount() > 0) {
                return true;
            } else {
                throw new ApplicationException("Không tìm thấy hoặc không cập nhật được bạn đọc");
            }
        }
    }
    
// check xem user có sách đang mượn hay không?

    public boolean userHasBorrowBook(ObjectId userId) {
        try (MongoClient client = DBUtils.open()) {
            MongoCollection<org.bson.Document> booksCollection = DBUtils.getDatabase(client)
                    .getCollection("BorrowBook");
            // Tìm sách dựa trên userId
            BasicDBObject query = new BasicDBObject();
            query.put("user_id", userId);
            query.put("borrow_status", "Đang mượn");
            
            FindIterable<org.bson.Document> iterable = booksCollection.find(query);
            // Kiểm tra xem có user nào có sách mượn không
            return iterable.iterator().hasNext(); // chỗ này tra về đúng sai 
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
// xóa user thôi

    public boolean deleteUser(ObjectId id) throws ApplicationException {
        Bson filter = Filters.eq("_id", id);
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<User> collection = database.getCollection(User.COLLECTION, User.class);

            long deletedCount = collection.deleteOne(filter).getDeletedCount();
            return deletedCount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //đếm user hiện có
    public long countUsers() {
        MongoClient client = DBUtils.open();
        MongoDatabase database = DBUtils.getDatabase(client);
        MongoCollection<Document> collection = database.getCollection("User");
        long count = collection.countDocuments();
        return count;
    }

    public List<User> findUserByName(String name) {
        List<User> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            MongoCollection<User> collection = DBUtils.getDatabase(client).getCollection("User", User.class);
            Pattern regex = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
            Bson query = Filters.regex("name", regex);
            

            try (MongoCursor<User> cursor = collection.find(query).iterator()) {
                while (cursor.hasNext()) {
                    results.add(cursor.next());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return results;
    }
}
