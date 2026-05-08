package com.cloud.util;

import java.nio.charset.StandardCharsets;

public class Base64Util {

    private static final java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
    private static final java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();

    /**
     * Base64编码
     * @param text 原始字符串
     * @return 编码后的字符串
     */
    public static String encode(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return encoder.encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64编码（字节数组）
     * @param bytes 原始字节数组
     * @return 编码后的字符串
     */
    public static String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return encoder.encodeToString(bytes);
    }

    /**
     * Base64解码
     * @param encodedText 编码后的字符串
     * @return 解码后的原始字符串
     */
    public static String decode(String encodedText) {
        if (encodedText == null || encodedText.isEmpty()) {
            return encodedText;
        }
        byte[] bytes = decoder.decode(encodedText);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Base64解码为字节数组
     * @param encodedText 编码后的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decodeToBytes(String encodedText) {
        if (encodedText == null || encodedText.isEmpty()) {
            return new byte[0];
        }
        return decoder.decode(encodedText);
    }

    /**
     * URL安全的Base64编码（用于URL参数）
     * @param text 原始字符串
     * @return 编码后的字符串
     */
    public static String encodeUrlSafe(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return java.util.Base64.getUrlEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * URL安全的Base64解码
     * @param encodedText 编码后的字符串
     * @return 解码后的原始字符串
     */
    public static String decodeUrlSafe(String encodedText) {
        if (encodedText == null || encodedText.isEmpty()) {
            return encodedText;
        }
        byte[] bytes = java.util.Base64.getUrlDecoder().decode(encodedText);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
