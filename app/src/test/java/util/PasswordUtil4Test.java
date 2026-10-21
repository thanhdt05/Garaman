package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử mã hóa mật khẩu PBKDF2 (PasswordUtil4)")
class PasswordUtil4Test {

    @Test
    @DisplayName("Băm mật khẩu tạo chuỗi đúng định dạng pbkdf2_sha256$600000$salt$hash")
    void testPasswordHashFormat() {
        String hash = PasswordUtil4.hashPassword("123456".toCharArray());
        assertNotNull(hash);
        String[] parts = hash.split("\\$");
        assertEquals(4, parts.length);
        assertEquals("pbkdf2_sha256", parts[0]);
        assertEquals("600000", parts[1]);
        assertFalse(parts[2].isEmpty());
        assertFalse(parts[3].isEmpty());
    }

    @Test
    @DisplayName("Xác thực mật khẩu đúng và sai")
    void testValidatePassword() {
        String password = "SecretPassword@123";
        String hash = PasswordUtil4.hashPassword(password.toCharArray());

        assertTrue(PasswordUtil4.validatePassword(password, hash));
        assertTrue(PasswordUtil4.verifyPassword(password, hash));

        assertFalse(PasswordUtil4.validatePassword("WrongPassword", hash));
        assertFalse(PasswordUtil4.validatePassword("", hash));
        assertFalse(PasswordUtil4.validatePassword((String) null, hash));
        assertFalse(PasswordUtil4.validatePassword(password, null));
    }

    @Test
    @DisplayName("Khớp mật khẩu '123456' với hash mẫu trong seed.sql")
    void testSeedPasswordHash() {
        String seedHash = "pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=";
        assertTrue(PasswordUtil4.verifyPassword("123456", seedHash));
        assertFalse(PasswordUtil4.verifyPassword("sai_mat_khau", seedHash));
    }
}
