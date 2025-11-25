import util.AESUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class TestAESDecrypt {
//    public static void main(String[] args) {
//        // Chuỗi password từ JSON
//        String encryptedPassword = ":f ?3N\r????T? i??Q?aZ??N(e*? ?F?";
//
//        // Key tương ứng
//        String aesKey = "db_warehouse";
//
//        try {
//            String decrypted = decryptAES(encryptedPassword, aesKey);
//            System.out.println("Decrypted password: " + decrypted);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public static String decryptAES(String encryptedBase64, String key) {
//        try {
//            byte[] decoded = Base64.getDecoder().decode(encryptedBase64);
//            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
//            byte[] decrypted = cipher.doFinal(decoded);
//            return new String(decrypted, "UTF-8");
//        } catch (Exception e) {
//            throw new RuntimeException("AES decode failed", e);
//        }
//    }
}
