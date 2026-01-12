import com.mongodb.client.*;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class MongoDB {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    public static void connect() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("voting_system");
            System.out.println("MongoDB connected");
        } catch (Exception e) {
            System.out.println("MongoDB connection failed: " + e.getMessage());
        }
    }
    
    public static void saveUser(String fullName, String email, String voterId, String passwordHash) {
        MongoCollection<Document> users = database.getCollection("users");
        Document user = new Document("fullName", fullName)
            .append("email", email)
            .append("voterId", voterId)
            .append("passwordHash", passwordHash)
            .append("hasVoted", false);
        users.insertOne(user);
    }
    
    public static Document getUser(String username) {
        MongoCollection<Document> users = database.getCollection("users");
        Document query = new Document("$or", List.of(
            new Document("email", username),
            new Document("voterId", username)
        ));
        return users.find(query).first();
    }
    
    public static void markAsVoted(String voterId) {
        MongoCollection<Document> users = database.getCollection("users");
        users.updateOne(
            new Document("voterId", voterId),
            new Document("$set", new Document("hasVoted", true))
        );
    }
    
    public static void saveCandidate(int id, String name, String party, String avatar) {
        MongoCollection<Document> candidates = database.getCollection("candidates");
        Document candidate = new Document("id", id)
            .append("name", name)
            .append("party", party)
            .append("avatar", avatar)
            .append("votes", 0);
        candidates.insertOne(candidate);
    }
    
    public static List<Document> getCandidates() {
        MongoCollection<Document> candidates = database.getCollection("candidates");
        List<Document> list = new ArrayList<>();
        candidates.find().into(list);
        return list;
    }
    
    public static void incrementVote(int candidateId) {
        MongoCollection<Document> candidates = database.getCollection("candidates");
        candidates.updateOne(
            new Document("id", candidateId),
            new Document("$inc", new Document("votes", 1))
        );
    }
    
    public static void saveVote(String voterId, int candidateId, String candidateName) {
        MongoCollection<Document> votes = database.getCollection("votes");
        Document vote = new Document("voterId", voterId)
            .append("candidateId", candidateId)
            .append("candidateName", candidateName)
            .append("timestamp", System.currentTimeMillis());
        votes.insertOne(vote);
    }
}
