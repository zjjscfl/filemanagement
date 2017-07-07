/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author ubuntu
 */
public class CodecHelper {

    static byte[] generateUUIDv4() {
        byte[] uuid = new byte[16];
        new Random().nextBytes(uuid);
        uuid[6] = (byte) ((uuid[6] & 0xf) | 0x40);
        uuid[8] = (byte) ((uuid[8] & 0x3f) | 0x80);
        return uuid;
    }

    // 此GUID经BASE64转义，并将会引起URL歧义的+/转换为-_
    public static String generateBase64GUID() {
        byte[] uuid = generateUUIDv4();
        //Base64 codec = new Base64( true );
        //return codec.encodeAsString( uuid );
        return Base64.encodeBase64URLSafeString(uuid);
    }

    public static String calcMD5(byte[] src) {
        MessageDigest digest = null;
        try {
            digest = java.security.MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
        digest.update(src);
        return Hex.encodeHexString(digest.digest());
    }
}
