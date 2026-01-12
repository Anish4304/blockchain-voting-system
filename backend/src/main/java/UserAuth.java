import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserAuth {
    private static Map<String, User> users = new ConcurrentHashMap<>();
    
    static class User {
        String fullName;
        String email;
        String voterId;
        String passwordHash;
        boolean hasVoted;
        
        User(String fullName, String email, String voterId, String passwordHash) {
            this.fullName = fullName;
            this.email = email;
            this.voterId = voterId;
            this.passwordHash = passwordHash;
            this.hasVoted = false;
        }
        
        String toJson() {
            return String.format("{\"fullName\":\"%s\",\"email\":\"%s\",\"voterId\":\"%s\",\"hasVoted\":%b}",
                fullName, email, voterId, hasVoted);
        }
    }
    
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean register(String fullName, String email, String voterId, String password) {
        if (users.containsKey(email) || users.containsKey(voterId)) {
            return false;
        }
        
        String passwordHash = hashPassword(password);
        User user = new User(fullName, email, voterId, passwordHash);
        users.put(email, user);
        users.put(voterId, user);
        return true;
    }
    
    public static User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.passwordHash.equals(hashPassword(password))) {
            return user;
        }
        return null;
    }
    
    public static User getUserByVoterId(String voterId) {
        return users.get(voterId);
    }
    
    public static void markAsVoted(String voterId) {
        User user = users.get(voterId);
        if (user != null) {
            user.hasVoted = true;
        }
    }
}
