import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Blockchain {
    private List<Block> chain;
    
    static class Block {
        int index;
        long timestamp;
        String data;
        String previousHash;
        String hash;
        int nonce;
        
        Block(int index, String data, String previousHash) {
            this.index = index;
            this.timestamp = new Date().getTime();
            this.data = data;
            this.previousHash = previousHash;
            this.nonce = 0;
            this.hash = calculateHash();
        }
        
        String calculateHash() {
            try {
                String input = index + timestamp + data + previousHash + nonce;
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(input.getBytes());
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
        
        void mineBlock(int difficulty) {
            String target = new String(new char[difficulty]).replace('\0', '0');
            while (!hash.substring(0, difficulty).equals(target)) {
                nonce++;
                hash = calculateHash();
            }
            System.out.println("Block mined: " + hash);
        }
        
        String toJson() {
            return String.format("{\"index\":%d,\"timestamp\":%d,\"data\":\"%s\",\"previousHash\":\"%s\",\"hash\":\"%s\",\"nonce\":%d}",
                index, timestamp, data, previousHash, hash, nonce);
        }
    }
    
    public Blockchain() {
        chain = new ArrayList<>();
        chain.add(createGenesisBlock());
    }
    
    private Block createGenesisBlock() {
        return new Block(0, "Genesis Block", "0");
    }
    
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }
    
    public void addBlock(String data) {
        Block previousBlock = getLatestBlock();
        Block newBlock = new Block(chain.size(), data, previousBlock.hash);
        newBlock.mineBlock(2);
        chain.add(newBlock);
    }
    
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                return false;
            }
            
            if (!currentBlock.previousHash.equals(previousBlock.hash)) {
                return false;
            }
        }
        return true;
    }
    
    public String getChainJson() {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < chain.size(); i++) {
            json.append(chain.get(i).toJson());
            if (i < chain.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
    
    public int getChainLength() {
        return chain.size();
    }
}
