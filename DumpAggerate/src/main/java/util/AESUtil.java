package util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    /**
     * Giải mã AES (key phải trùng với key khi mã hóa)
     * @param encryptedBase64 Chuỗi AES đã mã hóa, dạng Base64
     * @param key Chuỗi key AES
     * @return Chuỗi giải mã
     */
    public static String decryptAES(String encryptedBase64, String key) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedBase64);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("AES decode failed", e);
        }
    }
}
