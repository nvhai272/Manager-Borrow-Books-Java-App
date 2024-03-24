/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import exception.ApplicationException;
import model.Admin;
import model.Author;
import model.Book;
import model.NewOrUpdateBook;
import util.DBUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class BookDAO {
// trả về id của đối tượng theo isbn

    public ObjectId findIdBookByIsbn(String isbn) {
        Book book = this.findBookByIsbn(isbn);
        if (book != null) {
            return book.getId();
        }
        return null;
    }

    //trả về thông tin đối tượng book theo isbn 
    public Book findBookByIsbn(String isbn) {
        try (MongoClient client = DBUtils.open()) {
            return DBUtils.getDatabase(client)
                    .getCollection(Book.COLLECTION, Book.class)
                    .find(Filters.eq("isbn", isbn))
                    .first();
        }
    }
// trả về đối tượng theo id

    public Book findBookById(ObjectId bookId) {
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase db = DBUtils.getDatabase(client);
            MongoCollection<Book> collection = db.getCollection(Book.COLLECTION, Book.class);
            return collection.find(Filters.eq("_id", bookId))
                    .first();
        }
    }
// chỗ này gọi hàm từ BorrowBookDAO

    Map<ObjectId, Book> findBookByIds(MongoClient client, Set<ObjectId> bookIds) {
        Map<ObjectId, Book> map = new HashMap<>();
        MongoDatabase db = DBUtils.getDatabase(client);
        MongoCollection<Book> collection = db.getCollection(Book.COLLECTION, Book.class);
        Set<ObjectId> idObjs = bookIds.stream()
                .collect(Collectors.toSet());
        Bson filter = Filters.in("_id", idObjs);
        collection.find(filter)
                .forEach(it -> map.put(it.getId(), it));
        return map;
    }

// tìm tất cả sách 
    public List<Book> findAllBooks() {
        List<Book> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(Book.COLLECTION, Book.class)
                    .find()
                    //                    .limit(10).skip(0)
                    //.forEach(it -> results.add(it));
                    .forEach(results::add);

            Set<ObjectId> authorIds = results.stream()
                    .map(Book::getAuthorId)
                    .collect(Collectors.toSet());

//            Set<ObjectId> adminIds = results.stream()
//                .map(Book::getAdminId)
//                    .collect(Collectors.toSet());
            Map<ObjectId, Author> authorMap = new AuthorDAO().findAuthorByIds(client, authorIds);
//            Map<ObjectId, Admin> adminMap = new AdminDAO().findAdminByIds(client, adminIds);

            results.forEach(b -> b.setAuthor(authorMap.get(b.getAuthorId())));
//            results.forEach(b -> b.setAdmin(adminMap.get(b.getAdminId())));
        }
        return results;
    }
// thêm sách mới

    public boolean addNewBook(Book newBook) throws ApplicationException {
        Date now = new Date();
        Book bo = new Book();
        bo.setIsbn(newBook.getIsbn());
//        bo.setAdminId(newBook.getAdminId());
        bo.setAuthorId(newBook.getAuthorId());
        bo.setBookTitle(newBook.getBookTitle());
        bo.setPrice(newBook.getPrice());
        bo.setQuantity(newBook.getQuantity());
        bo.setRemain(newBook.getRemain());
        bo.setCreateAt(now);
        bo.setUpdateAt(now);
        bo.setStatus(newBook.getStatus());
        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(Book.COLLECTION, Book.class)
                    .insertOne(bo);
        }
        return true;
    }
// cập nhật sách nhung k update dc tac gia va admin

    public boolean updateBook(ObjectId id, NewOrUpdateBook newBook) throws ApplicationException {
        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.combine(
                Updates.set("isbn", newBook.getIsbn()),
                Updates.set("book_title", newBook.getBookTitle()),
                //                Updates.set("adminId", newBook.getAdminId()),
                //                Updates.set("authorId", newBook.getAuthorId()),
                Updates.set("price", newBook.getPrice()),
                Updates.set("quantity", newBook.getQuantity()),
                Updates.set("remain", newBook.getRemain()),
                Updates.set("updated_at", new Date()),
                Updates.set("status", newBook.getStatus())
        );

        try (MongoClient client = DBUtils.open()) {
            UpdateResult updateResult = DBUtils.getDatabase(client)
                    .getCollection(Book.COLLECTION, Book.class)
                    .updateOne(filter, update);
            if (updateResult.getModifiedCount() > 0) {

            }
        }
        return true;
    }
// kiểm tra xem sách có đang được mượn không

    public boolean checkBookWasBorrowed(ObjectId bookId) {
        try (MongoClient client = DBUtils.open()) {
            MongoCollection<org.bson.Document> booksCollection = DBUtils.getDatabase(client)
                    .getCollection("BorrowBook");
            // Tìm sách dựa trên bookId
            BasicDBObject query = new BasicDBObject("book_id", bookId);
            FindIterable<org.bson.Document> iterable = booksCollection.find(query);
            // Kiểm tra xem có user nào có sách mượn không
            return iterable.iterator().hasNext();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // xóa sách
    public boolean deleteBook(ObjectId id) {
        Bson filter = Filters.eq("_id", id);
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<Book> collection = database.getCollection(Book.COLLECTION, Book.class);
            // Xóa dữ liệu từ MongoDB
            return collection.deleteOne(filter).getDeletedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
//đếm sách

    public long countBooks() {
        MongoClient client = DBUtils.open();
        MongoDatabase database = DBUtils.getDatabase(client);
        MongoCollection<Document> collection = database.getCollection("Book");
        long count = collection.countDocuments();
        return count;
    }

    // tính tổng số lượng sách đang có
    public long totalBooks() {
        long total = 0;
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<Document> collection = database.getCollection("Book");

            Document groupStage = new Document("$group",
                    new Document("_id", null)
                            .append("total", new Document("$sum", "$quantity"))
            );

            AggregateIterable<Document> result = collection.aggregate(Collections.singletonList(groupStage));

            for (Document doc : result) {
                Object totalObj = doc.get("total");
                if (totalObj instanceof Number) {
                    total = ((Number) totalObj).longValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
// tìm kiếm theo tên sách

    public List<Book> findBookByName(String name) {
        List<Book> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            MongoCollection<Book> collection = DBUtils.getDatabase(client).getCollection("Book", Book.class);
            Pattern regex = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
            Bson query = Filters.eq("book_title", regex);

            try (MongoCursor<Book> cursor = collection.find(query).iterator()) {
                while (cursor.hasNext()) {
                    results.add(cursor.next());
                }
            }
        } catch (Exception e) {
        }
        return results;
    }

    public boolean findBookByISBN(String isbn) {
        try (MongoClient client = DBUtils.open()) {
            FindIterable<Book> foundBooks = DBUtils.getDatabase(client)
                    .getCollection(Book.COLLECTION, Book.class)
                    .find(Filters.eq("isbn", isbn));

            return foundBooks.iterator().hasNext();
        }
    }
}
