package yc.star.forum.utils;


import java.util.UUID;

public class UUIDUtil {

    // 生成原始的36位uuid
    public static String uuid () {
        return UUID.randomUUID().toString();
    }

    // 生成处理后的32位uuid
    public static String uuidSalt () {
        return UUID.randomUUID().toString().replace("-","");
    }

}
