import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitcoinMiner {

    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final String TARGET_PREFIX = "000000"; // Simplified mining target for demonstration

    public static void main(String[] args) {
        System.out.println("Starting mining with " + NUM_THREADS + " threads...");

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(new MinerTask(i));
        }

        executor.shutdown();
    }

    static class MinerTask implements Runnable {
        private final int threadId;
        private final Random random = new Random();

        MinerTask(int threadId) {
            this.threadId = threadId;
        }

        @Override
        public void run() {
            try {
                long nonce = random.nextLong();
                while (true) {
                    String blockHeader = createBlockHeader(nonce);
                    String hash = doubleSha256(blockHeader);

                    if (hash.startsWith(TARGET_PREFIX)) {
                        System.out.println("Thread " + threadId + " found a valid hash: " + hash + " (nonce: " + nonce + ")");
                        break;
                    }

                    nonce++;
                }
            } catch (NoSuchAlgorithmException e) {
                System.err.println("SHA-256 algorithm not found: " + e.getMessage());
            }
        }

        private String createBlockHeader(long nonce) {
            // Simplified block header fields for demonstration
            String version = "1";                               // Block version
            String prevBlockHash = "000000000000000000000000"; // Previous block hash (placeholder)
            String merkleRoot = "4d5e5f6a7b8c9d0e";            // Simplified Merkle root (placeholder)
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000); // Current timestamp
            String difficultyTarget = "1d00ffff";              // Simplified difficulty target

            // Combine fields to simulate a block header
            return version + prevBlockHash + merkleRoot + timestamp + difficultyTarget + nonce;
        }

        private String doubleSha256(String data) throws NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] firstHash = digest.digest(data.getBytes());
            byte[] secondHash = digest.digest(firstHash);

            // Convert the hash to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : secondHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }
}
