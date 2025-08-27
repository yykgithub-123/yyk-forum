package yc.star.forum.service;

import yc.star.forum.model.User;

/**
 * 用户服务接口，用于前后端分离项目
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param user 包含用户名和密码的用户对象
     * @return 登录成功返回用户信息，失败返回null
     */
    User login(User user);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册成功返回true，失败返回false
     */
    boolean register(User user);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    boolean isUsernameExist(String username);
    
    /**
     * 获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserInfo(Long id);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新成功返回true，失败返回false
     */
    boolean updateUserInfo(User user);
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 更新成功返回true，失败返回false
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
} 