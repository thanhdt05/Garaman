package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil4 {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_LENGTH_BITS = 256;
    private static final String PREFIX = "pbkdf2_sha256";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil4() {
        // Private constructor
    }

    public static String hashPassword(char[] password) {
        if (password == null) {
            throw new IllegalArgumentException("Mật khẩu không được null");
        }
        try {
            byte[] salt = new byte[SALT_BYTES];
            SECURE_RANDOM.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();

            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);

            return PREFIX + "$" + ITERATIONS + "$" + saltB64 + "$" + hashB64;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Lỗi khởi tạo thuật toán mã hóa mật khẩu", e);
        }
    }

    public static boolean validatePassword(String password, String storedHash) {
        if (password == null) {
            return false;
        }
        return validatePassword(password.toCharArray(), storedHash);
    }

    public static boolean verifyPassword(String password, String storedHash) {
        return validatePassword(password, storedHash);
    }

    public static boolean validatePassword(char[] password, String storedHash) {
        if (password == null || storedHash == null || storedHash.trim().isEmpty()) {
            return false;
        }

        String[] parts = storedHash.split("\\$");
        if (parts.length != 4) {
            return false;
        }

        if (!PREFIX.equals(parts[0])) {
            return false;
        }

        int iterations;
        try {
            iterations = Integer.parseInt(parts[1]);
            if (iterations <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        byte[] salt;
        byte[] expectedHash;
        try {
            salt = Base64.getDecoder().decode(parts[2]);
            expectedHash = Base64.getDecoder().decode(parts[3]);
        } catch (IllegalArgumentException e) {
            return false;
        }

        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, expectedHash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(expectedHash, testHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String pass = args.length > 0 ? args[0] : "123456";
        String hash = hashPassword(pass.toCharArray());
        System.out.println("Mật khẩu: " + pass);
        System.out.println("Hash tạo được: " + hash);
        boolean verify = validatePassword(pass.toCharArray(), hash);
        System.out.println("Kiểm tra khớp: " + verify);
    }
}
