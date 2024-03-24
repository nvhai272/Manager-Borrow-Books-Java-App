/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import exception.ApplicationException;
import model.NewOrUpdateRule;
import model.Rule;
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

/**
 *
 * @author hainv
 */
public class RuleDAO {

    public Map<ObjectId, Rule> findRuleByIds(MongoClient client, Set<ObjectId> ids) {
        Map<ObjectId, Rule> map = new HashMap<>();
        MongoDatabase db = DBUtils.getDatabase(client);
        MongoCollection<Rule> collection = db.getCollection(Rule.COLLECTION, Rule.class);
        Set<ObjectId> idObjs = ids.stream()
                .collect(Collectors.toSet());
        Bson filter = Filters.in("_id", idObjs);
        collection.find(filter)
                .forEach(it -> map.put(it.getId(), it));
        return map;
    }

    public List<Rule> findAllRules() {
        List<Rule> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(Rule.COLLECTION, Rule.class)
                    .find()
                    .forEach(results::add);
        }
        return results;
    }

    public boolean addNewRule(NewOrUpdateRule newRule) {
        Rule rule = new Rule();
        rule.setType(newRule.getType());
        rule.setFine(newRule.getFine());
        rule.setMaxFine(newRule.getFine());

        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(Rule.COLLECTION, Rule.class)
                    .insertOne(rule);
        }
        return true;
    }

    public boolean deleteRule(ObjectId id, NewOrUpdateRule newRule) {
         Bson filter = Filters.eq("_id", id);
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<Rule> collection = database.getCollection(Rule.COLLECTION, Rule.class);
            // Xóa dữ liệu từ MongoDB
            long deletedCount = collection.deleteOne(filter).getDeletedCount();
            return deletedCount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRule(ObjectId id, NewOrUpdateRule newRule) throws ApplicationException {
        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.combine(
                Updates.set("type", newRule.getType()),
                Updates.set("fine", newRule.getFine()),
                Updates.set("max_fine", newRule.getMaxFine() )
        );
        try (MongoClient client = DBUtils.open()) {
            UpdateResult updateResult = DBUtils.getDatabase(client)
                    .getCollection(Rule.COLLECTION,Rule.class)
                    .updateOne(filter, update);
            if (updateResult.getModifiedCount() > 0) {
                return true; // Cập nhật thành công
            } else {
                throw new ApplicationException("Không tìm thấy hoặc không cập nhật được rule");
            }
        }
    }
}
