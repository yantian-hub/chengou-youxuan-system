package com.cloud.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class MD5 {

    /**
     * MD5加密
     */
    public static String encrypt(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不存在", e);
        }
    }

    /**
     * 加盐MD5加密
     * @param text 明文
     * @param salt 盐值
     */
    public static String encryptWithSalt(String text, String salt) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return encrypt(text + "{" + salt + "}");
    }

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
