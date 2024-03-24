/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import model.BorrowBook;
import util.DBUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import model.Book;
import model.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import util.DateTimeUtils;

/**
 *
 * @author hainv
 */
public class BorrowBookDAO {

    public BorrowBook findBorrowBookById(ObjectId id) {
        BorrowBook bb = null;
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<BorrowBook> collection = database.getCollection(BorrowBook.COLLECTION, BorrowBook.class);
            BorrowBook foundBorrowBook = collection.find(Filters.eq("_id", id)).first();
            return foundBorrowBook;
        } catch (MongoException e) {            
            return bb;
        }
    }
// cập nhật trả sách
    public BorrowBook updateBorrowBook(ObjectId id) {
        BorrowBook bb = null;
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<Document> collection = database.getCollection("BorrowBook");
            Document borrowBookDoc = collection.find(Filters.eq("_id", id)).first();

            if (borrowBookDoc != null) {
                BorrowBook foundBorrowBook = mapToBorrowBook(borrowBookDoc);
                Date currentDate = new Date();
                LocalDate localDate1 = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate localDate2 = foundBorrowBook.getDurationBorrow().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                long daysBetween = ChronoUnit.DAYS.between(localDate2, localDate1);
                collection.findOneAndUpdate(
                        Filters.eq("_id", id),
                        Updates.combine(
                                Updates.set("return_date", currentDate),
                                Updates.set("duration", (int) daysBetween),
                                Updates.set("fine_amount", calculateFineAmount((int) daysBetween)),
                                Updates.set("fine_reason", generateFineReason((int) daysBetween))
                        )
                );

                return foundBorrowBook;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bb;
    }

    // cập nhật stt trả
    public void updateStt(ObjectId objectId) {
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<Document> collection = database.getCollection("BorrowBook");
            collection.updateOne(
                    Filters.eq("_id", objectId),
                    Updates.set("borrow_status", "Đã trả")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // cập nhật remian trả
    public void updateRemainReturn(String isbn) {
        BookDAO bookDAO = new BookDAO();
        Book book = bookDAO.findBookByIsbn(isbn);

        if (true) {
            try (MongoClient client = DBUtils.open()) {
                MongoDatabase database = DBUtils.getDatabase(client);
                MongoCollection<Book> collection = database.getCollection(Book.COLLECTION, Book.class);

                collection.updateOne(Filters.eq("_id", book.getId()),
                        Updates.set("remain", book.getRemain() + 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//cập nhật remain mượn
    public void updateRemainBorrow(String isbn) {
        BookDAO bookDAO = new BookDAO();
        Book book = bookDAO.findBookByIsbn(isbn);

        if (true) {
            try (MongoClient client = DBUtils.open()) {
                MongoDatabase database = DBUtils.getDatabase(client);
                MongoCollection<Book> collection = database.getCollection(Book.COLLECTION, Book.class);

                collection.updateOne(Filters.eq("_id", book.getId()),
                        Updates.set("remain", book.getRemain() - 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
// kiểm tra điều kiện sách để cho mượn sách

    public long checkRemainOfBookAndStatusBook(String isbn) {
        BookDAO bookDAO = new BookDAO();
        Book bookId = bookDAO.findBookByIsbn(isbn);
        // doc cmt tiep theo
        System.out.println("Book Id: " + bookId.getId());
        System.out.println(bookId.getRemain());
        System.out.println(bookId.getStatus());

        try (MongoClient client = DBUtils.open()) {
            return DBUtils.getDatabase(client)
                    .getCollection(Book.COLLECTION, Book.class)
                    .countDocuments(Filters.and(
                            Filters.eq("_id", bookId.getId()),
                            //lay id hoac isbn deu dc vi no la duy nhat
                            Filters.eq("status", "Có thể mượn"),
                            Filters.gt("remain", 0)
                    )
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
// đếm số sách mà User đang mượn để check điều kiện cho mượn

    public long getNumberOfBooksBorrowedByUser(String phoneNo) {
        UserDAO userDAO = new UserDAO();
        Object userId = userDAO.findUserByPhoneNo(phoneNo).getId();
        System.out.println("User Id: " + userId);
// Nếu là kiểu dữ liệu Object thì bên dưới filter k lấy được các thông tin khác của đối tượng
        try (MongoClient client = DBUtils.open()) {
            return DBUtils.getDatabase(client)
                    .getCollection(BorrowBook.COLLECTION, BorrowBook.class)
                    .countDocuments(Filters.and(
                            Filters.eq("user_id", userId),
                            Filters.eq("borrow_status", "Đang mượn")
                    )
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
// thêm mượn sách thôi

    public boolean newBorrow(String phoneNo, String isbn, int numberDayBorrow) {
        BookDAO bookDAO = new BookDAO();
        UserDAO userDAO = new UserDAO();
        BorrowBook borrowBook = new BorrowBook();
        LocalDate now = LocalDate.now();

        borrowBook.setUserId(userDAO.findIdUserByPhoneNo(phoneNo));
        borrowBook.setBookId(bookDAO.findIdBookByIsbn(isbn));

        LocalDate returnDeadline = now.plusDays(numberDayBorrow);

        borrowBook.setBorrowedDate(DateTimeUtils.toDate(now));
        borrowBook.setDurationBorrow(DateTimeUtils.toDate(returnDeadline));
        borrowBook.setBorrowStatus("Đang mượn");

        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(BorrowBook.COLLECTION, BorrowBook.class)
                    .insertOne(borrowBook);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// đếm số sách đang được mượn
    public long countBorrowBooks() {
        MongoClient client = DBUtils.open();
        MongoDatabase database = DBUtils.getDatabase(client);
        MongoCollection<Document> collection = database.getCollection("BorrowBook");
        Bson filter = Filters.eq("borrow_status", "Đang mượn");
        long count = collection.countDocuments(filter);
        return count;
    }

    public float calculateFineAmount(int duration) {
        float maxFine = 200000;
        float finePerDay = 2000;
        float fineAmount = finePerDay * duration;
        if (duration <= 0) {
            return 0;
        }
        if (fineAmount > maxFine) {
            return maxFine;
        }
        return fineAmount;
    }

    public String generateFineReason(int duration) {
        if (duration <= 0) {
            return "Chưa quá hạn";
        }
        return "Quá hạn " + duration + " ngày";
    }

    private BorrowBook mapToBorrowBook(Document document) {
        BorrowBook borrowBook = new BorrowBook();
        borrowBook.setId(document.getObjectId("_id"));
        borrowBook.setDurationBorrow(document.getDate("duration_borrow"));

        return borrowBook;
    }

    public static List<BorrowBook> findBB() {
        List<BorrowBook> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            Bson filter = Filters.eq("borrow_status", "Đang mượn");

            DBUtils.getDatabase(client)
                    .getCollection(BorrowBook.COLLECTION, BorrowBook.class)
                    .find(filter)
                    .sort(Sorts.descending("$natural")) // Sắp xếp ngược lại theo thứ tự tự nhiên
                    .forEach(results::add);
            return results;
        }
    }

    //tim het sách
    public List<BorrowBook> findAllBorrowBooks() {
        List<BorrowBook> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            DBUtils.getDatabase(client)
                    .getCollection(BorrowBook.COLLECTION, BorrowBook.class)
                    .find()
                    //                    .limit(10).skip(0)
                    //                    .forEach(it -> results.add(it))
                    .forEach(results::add);

            Set<ObjectId> bookIds = results.stream()
                    .map(BorrowBook::getBookId)
                    .collect(Collectors.toSet());

            Set<ObjectId> userIds = results.stream()
                    .map(BorrowBook::getUserId)
                    .collect(Collectors.toSet());

//            Set<ObjectId> adminIds = results.stream()
//                    .map(BorrowBook::getAdminId)
//                    .collect(Collectors.toSet());
            Map<ObjectId, Book> bookMap = new BookDAO().findBookByIds(client, bookIds);

            Map<ObjectId, User> userMap = new UserDAO().findUserByIds(client, userIds);

//            Map<ObjectId, Admin> adminMap = new AdminDAO().findAdminByIds(client, adminIds);
            results.forEach(b -> b.setBook(bookMap.get(b.getBookId())));

            results.forEach(b -> b.setUser(userMap.get(b.getUserId())));
//            results.forEach(b -> b.setAdmin(adminMap.get(b.getAdminId())));
        }
        return results;
    }

    // find theo ISBN and status_book 
    public List<BorrowBook> findBorrowBooksByUserIdAndBookId(String status, Object bookId) {
        List<BorrowBook> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            MongoCollection<BorrowBook> collection = DBUtils.getDatabase(client).getCollection("BorrowBook", BorrowBook.class);

            Bson query = Filters.and(Filters.eq("borrow_status", status), Filters.eq("book_id", bookId));

            try (MongoCursor<BorrowBook> cursor = collection.find(query).iterator()) {
                while (cursor.hasNext()) {
                    results.add(cursor.next());
                }
            }
        } catch (Exception e) {
        }
        return results;
    }

    // find theo status
    public List<BorrowBook> findBorrowBooksByStt(String status) {
        List<BorrowBook> results = new ArrayList<>();
        try (MongoClient client = DBUtils.open()) {
            MongoCollection<BorrowBook> collection = DBUtils.getDatabase(client).getCollection("BorrowBook", BorrowBook.class);

            Bson query = Filters.eq("borrow_status", status);

            try (MongoCursor<BorrowBook> cursor = collection.find(query).iterator()) {
                while (cursor.hasNext()) {
                    results.add(cursor.next());
                }
            }
        } catch (Exception e) {
        }
        return results;
    }

    // tim kiem doi tuong dang muon 
    public BorrowBook findBorrowByUserIdAndBookId(ObjectId bookId, ObjectId userId) {
        BorrowBook bb = new BorrowBook();
        try (MongoClient client = DBUtils.open()) {
            MongoCollection<BorrowBook> collection = DBUtils.getDatabase(client).getCollection("BorrowBook", BorrowBook.class);
            Bson query = Filters.and(
                    Filters.eq("user_id", userId),
                    Filters.eq("book_id", bookId),
                    Filters.eq("borrow_status", "Đang mượn"));
            bb = collection.find(query).first();

        } catch (Exception e) {
        }
        return bb;
    }

    public long countMostBorrowBooks(ObjectId bookId) {
        MongoClient client = DBUtils.open();
        MongoDatabase database = DBUtils.getDatabase(client);
        MongoCollection<Document> collection = database.getCollection("BorrowBook");
        Bson filter = Filters.eq("book_id", bookId);
        long count = collection.countDocuments(filter);
        return count;
    }

    // kiem tra xem trang thai muon da trả chưa 
    public boolean checkBookWasReturned(ObjectId bbId) {
        try (MongoClient client = DBUtils.open()) {
            MongoCollection<org.bson.Document> booksCollection = DBUtils.getDatabase(client)
                    .getCollection("BorrowBook");
            BasicDBObject query = new BasicDBObject();
            query.put("_id", bbId);
            query.put("borrow_status", "Đang mượn");
            FindIterable<org.bson.Document> iterable = booksCollection.find(query);
            return iterable.iterator().hasNext();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBorrowBook(ObjectId id) {
        Bson filter = Filters.eq("_id", id);
        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<BorrowBook> collection = database.getCollection(BorrowBook.COLLECTION, BorrowBook.class);
            return collection.deleteOne(filter).getDeletedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Document> getBorrowCounts() {
        List<Document> result = new ArrayList<>();

        try (MongoClient client = DBUtils.open()) {
            MongoDatabase database = DBUtils.getDatabase(client);
            MongoCollection<Document> collection = database.getCollection("BorrowBook");

            collection.aggregate(Arrays.asList(
                    Aggregates.group("$book_id", Accumulators.sum("borrow_count", 1)),
                    Aggregates.lookup("Book", "_id", "_id", "book_info"),
                    Aggregates.unwind("$book_info"),
                    
                    Aggregates.project(
                            Projections.fields(
                                    Projections.computed("author_id", "$book_info.author_id"),
                                    Projections.computed("isbn", "$book_info.isbn"),
                                    Projections.computed("book_title", "$book_info.book_title"),
                                    Projections.computed("borrow_count", "$borrow_count")
                            )
                    ),
                    Aggregates.sort(Sorts.descending("borrow_count"))
            ))
                    .into(result);  //Thay vì sử dụng 
//                    .iterator().forEachRemaining(result::add);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
