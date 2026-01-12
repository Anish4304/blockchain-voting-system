import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VotingServer {
    private static List<Candidate> candidates = new ArrayList<>();
    private static Set<String> voters = ConcurrentHashMap.newKeySet();
    private static Blockchain blockchain = new Blockchain();
    
    static class Candidate {
        int id;
        String name;
        String party;
        int votes;
        String avatar;
        
        Candidate(int id, String name, String party, int votes, String avatar) {
            this.id = id;
            this.name = name;
            this.party = party;
            this.votes = votes;
            this.avatar = avatar;
        }
        
        String toJson() {
            return String.format("{\"id\":%d,\"name\":\"%s\",\"party\":\"%s\",\"votes\":%d,\"avatar\":\"%s\"}", 
                id, name, party, votes, avatar);
        }
    }
    
    public static void main(String[] args) throws IOException {
        MongoDB.connect();
        initCandidates();
        System.out.println("Blockchain initialized");
        System.out.println("Genesis block created");
        
        HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0);
        
        server.createContext("/api/candidates", exchange -> {
            addCorsHeaders(exchange);
            if ("GET".equals(exchange.getRequestMethod())) {
                getCandidates(exchange);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                addCandidate(exchange);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
            }
        });
        
        server.createContext("/api/vote", exchange -> {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                vote(exchange);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
            }
        });
        
        server.createContext("/api/results", exchange -> {
            addCorsHeaders(exchange);
            if ("GET".equals(exchange.getRequestMethod())) {
                getResults(exchange);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
            }
        });
        
        server.createContext("/api/blockchain", exchange -> {
            addCorsHeaders(exchange);
            if ("GET".equals(exchange.getRequestMethod())) {
                getBlockchain(exchange);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
            }
        });
        
        server.createContext("/api/register", exchange -> {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                register(exchange);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
            }
        });
        
        server.createContext("/api/login", exchange -> {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                login(exchange);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
            }
        });
        
        server.setExecutor(null);
        server.start();
        System.out.println("Backend running on http://localhost:5000");
    }
    
    private static void initCandidates() {
        List<org.bson.Document> existing = MongoDB.getCandidates();
        if (existing.isEmpty()) {
            MongoDB.saveCandidate(1, "John Smith", "Democratic Party", "👨");
            MongoDB.saveCandidate(2, "Sarah Johnson", "Republican Party", "👩");
            MongoDB.saveCandidate(3, "Michael Chen", "Independent", "👨");
            MongoDB.saveCandidate(4, "Emily Davis", "Green Party", "👩");
            System.out.println("Initial candidates added to MongoDB");
        }
        
        for (org.bson.Document doc : MongoDB.getCandidates()) {
            candidates.add(new Candidate(
                doc.getInteger("id"),
                doc.getString("name"),
                doc.getString("party"),
                doc.getInteger("votes"),
                doc.getString("avatar")
            ));
        }
    }
    
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json");
    }
    
    private static void getCandidates(HttpExchange exchange) throws IOException {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < candidates.size(); i++) {
            json.append(candidates.get(i).toJson());
            if (i < candidates.size() - 1) json.append(",");
        }
        json.append("]");
        
        byte[] response = json.toString().getBytes();
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
    
    private static void vote(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        String candidateId = extractValue(body, "candidateId");
        String voterId = extractValue(body, "voterId");
        
        org.bson.Document user = MongoDB.getUser(voterId);
        if (user != null && user.getBoolean("hasVoted")) {
            sendError(exchange, "Already voted");
            return;
        }
        
        Candidate candidate = candidates.stream()
            .filter(c -> c.id == Integer.parseInt(candidateId))
            .findFirst().orElse(null);
            
        if (candidate == null) {
            sendError(exchange, "Candidate not found");
            return;
        }
        
        candidate.votes++;
        MongoDB.incrementVote(candidate.id);
        MongoDB.markAsVoted(voterId);
        MongoDB.saveVote(voterId, candidate.id, candidate.name);
        
        String voteData = String.format("Vote: %s for %s", voterId, candidate.name);
        blockchain.addBlock(voteData);
        System.out.println("Vote recorded on blockchain");
        
        String response = "{\"success\":true,\"candidate\":" + candidate.toJson() + "}";
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
    
    private static void addCandidate(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        String name = extractValue(body, "name");
        String party = extractValue(body, "party");
        
        String[] avatars = {"👨", "👩", "🧑", "👴", "👵"};
        String avatar = avatars[new Random().nextInt(avatars.length)];
        
        int newId = candidates.size() + 1;
        Candidate newCandidate = new Candidate(newId, name, party, 0, avatar);
        candidates.add(newCandidate);
        
        MongoDB.saveCandidate(newId, name, party, avatar);
        
        String candidateData = String.format("New Candidate: %s - %s", name, party);
        blockchain.addBlock(candidateData);
        System.out.println("Candidate added to blockchain");
        
        byte[] response = newCandidate.toJson().getBytes();
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
    
    private static void getResults(HttpExchange exchange) throws IOException {
        int totalVotes = candidates.stream().mapToInt(c -> c.votes).sum();
        
        StringBuilder json = new StringBuilder("[");
        List<Candidate> sorted = new ArrayList<>(candidates);
        sorted.sort((a, b) -> b.votes - a.votes);
        
        for (int i = 0; i < sorted.size(); i++) {
            Candidate c = sorted.get(i);
            double percentage = totalVotes > 0 ? (c.votes * 100.0 / totalVotes) : 0;
            json.append(String.format("{\"id\":%d,\"name\":\"%s\",\"party\":\"%s\",\"votes\":%d,\"avatar\":\"%s\",\"percentage\":%.1f}",
                c.id, c.name, c.party, c.votes, c.avatar, percentage));
            if (i < sorted.size() - 1) json.append(",");
        }
        json.append("]");
        
        byte[] response = json.toString().getBytes();
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
    
    private static void sendError(HttpExchange exchange, String message) throws IOException {
        String response = "{\"error\":\"" + message + "\"}";
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(400, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
    
    private static String extractValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) start++;
        
        int end = start;
        while (end < json.length() && json.charAt(end) != '"' && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        
        return json.substring(start, end);
    }
    
    private static void getBlockchain(HttpExchange exchange) throws IOException {
        String response = String.format("{\"chain\":%s,\"length\":%d,\"valid\":%b}",
            blockchain.getChainJson(), blockchain.getChainLength(), blockchain.isChainValid());
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
    
    private static void register(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        String fullName = extractValue(body, "fullName");
        String email = extractValue(body, "email");
        String voterId = extractValue(body, "voterId");
        String password = extractValue(body, "password");
        
        if (MongoDB.getUser(email) != null || MongoDB.getUser(voterId) != null) {
            sendError(exchange, "User already exists");
            exchange.close();
            return;
        }
        
        String passwordHash = UserAuth.hashPassword(password);
        MongoDB.saveUser(fullName, email, voterId, passwordHash);
        blockchain.addBlock("New Voter: " + fullName + " (" + voterId + ")");
        
        String response = "{\"success\":true}";
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
    
    private static void login(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        String username = extractValue(body, "username");
        String password = extractValue(body, "password");
        
        org.bson.Document user = MongoDB.getUser(username);
        if (user != null && user.getString("passwordHash").equals(UserAuth.hashPassword(password))) {
            String response = String.format("{\"success\":true,\"voter\":{\"fullName\":\"%s\",\"email\":\"%s\",\"voterId\":\"%s\",\"hasVoted\":%b}}",
                user.getString("fullName"), user.getString("email"), user.getString("voterId"), user.getBoolean("hasVoted"));
            byte[] bytes = response.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
        } else {
            sendError(exchange, "Invalid credentials");
        }
        exchange.close();
    }
}
