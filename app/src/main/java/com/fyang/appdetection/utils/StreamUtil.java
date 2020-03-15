package com.fyang.appdetection.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
    /**
     * 流转换成字符串
     *
     * @param is 流对象
     * @return 流转换成的字符串    返回null代表异常
     */
    public static String streamToString(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] buff = new byte[1024];
        int temp = -1;

        try {
            while ((temp = is.read(buff)) != -1) {
                bos.write(buff, 0, temp);
            }

            return bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
}
