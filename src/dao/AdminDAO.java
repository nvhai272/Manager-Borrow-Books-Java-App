/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import exception.ApplicationException;
import model.Admin;
import model.NewOrUpdateAdmin;
import util.DBUtils;
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
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class AdminDAO {
// gọi hàm từ BorrowBookDAO
    public Map<ObjectId, Admin> findAdminByIds(MongoClient client, Set<ObjectId> ids) {
        Map<ObjectId, Admin> map = new HashMap<>();
        MongoDatabase db = DBUtils.getDatabase(client);
        MongoCollection<Admin> collection = db.getCollection(Admin.COLLECTION, Admin.class);
        Set<ObjectId> idObjs = ids.stream()
                .collect(Collectors.toSet());
        Bson filter = Filters.in("_id", idObjs);
        collection.find(filter)
                .forEach(it -> map.put(it.getId(), it));
        return map;
    }
// tìm kiếm tất cả danh sách tác giả
    public List<Admin> findAllAdmins() {
        List<Admin> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(Admin.COLLECTION, Admin.class)
                    .find()
                    .forEach(results::add);
        }
        return results;
    }
// Thêm mới
    public boolean addNewAdmin(NewOrUpdateAdmin newAdmin) {
        Admin a = new Admin();
        Updates.set("name", newAdmin.getName());
        Updates.set("phone_no", newAdmin.getPhoneNo());
        Updates.set("password", newAdmin.getPassword());
        Updates.set("email", newAdmin.getEmail());

        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(Admin.COLLECTION, Admin.class)
                    .insertOne(a);
        }
        return true;
    }

    // update object admin theo Object id
    public boolean updateAdmin(ObjectId id, NewOrUpdateAdmin newAdmin) throws ApplicationException {
        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.combine(
                Updates.set("name", newAdmin.getName()),
                Updates.set("phone_no", newAdmin.getPhoneNo()),
                Updates.set("password", newAdmin.getPassword()),
                Updates.set("email", newAdmin.getEmail())
        );
        try (MongoClient client = DBUtils.open()) {
            UpdateResult updateResult = DBUtils.getDatabase(client)
                    .getCollection(Admin.COLLECTION, Admin.class)
                    .updateOne(filter, update);
            if (updateResult.getModifiedCount() > 0) {
                return true; 
            } else {
                throw new ApplicationException("Không tìm thấy hoặc không cập nhật được bạn đọc");
            }
        }
    }
//xóa thôi
    public boolean deleteAdmin(ObjectId id) throws ApplicationException {
        Bson filter = Filters.eq("_id", id);
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<Admin> collection = database.getCollection(Admin.COLLECTION, Admin.class);
            
            long deletedCount = collection.deleteOne(filter).getDeletedCount();
            return deletedCount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Admin findUserByPhoneNoForReg(String phone) {
        try (MongoClient client = DBUtils.open()) {
//            Bson roleFilter = Filters.eq("role", 0);
            Bson phoneFilter = Filters.eq("phone_no", phone);
            Bson filter = Filters.and(phoneFilter);
            Admin admin = DBUtils.getDatabase(client)
                    .getCollection(Admin.COLLECTION, Admin.class)
                    .find(filter)
                    .first();

//            Integer tenantsID = user.getTenantId();
//            Tenants tenant = new TenantDAO().findTenantById(tenantsID);
//            user.setTenant(tenant);
            return admin;
        }
    }
}
