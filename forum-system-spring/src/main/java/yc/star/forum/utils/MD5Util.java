package yc.star.forum.utils;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    // 对字符串进行MD5加密
    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    // 对密码进行加盐加密
    public static String md5Salt (String password,String salt) {
        return md5(md5(password)+salt);
    }

}
