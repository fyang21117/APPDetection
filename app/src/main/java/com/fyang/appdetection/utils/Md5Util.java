package com.fyang.appdetection.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

    /**
     * 给指定字符串按照md5算法去加密
     *
     * @param psd 需要加密的密码	加盐处理
     * @return md5后的字符串
     */
    public static String encoder(String psd) {
        try {
            psd = psd + "fujie";
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            byte[] digest = md5.digest(psd.getBytes());

            StringBuffer sb = new StringBuffer();

            for (byte b : digest) {
                int i = b & 0xff;
                String s = Integer.toHexString(i);
                if (s.length() < 2) {
                    s = "0" + s;
                }
                sb.append(s);
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
