package yc.star.forum.service;

import yc.star.forum.model.User;

import java.util.List;

/**
 * 用户service接口
 */
public interface IUserService {

    // 创建用户
    void createNormalUser(User user);

    // 登录验证
    User verifyLogin(User user);

    // 根据id获取用户信息
    User selectUserInfo(Long id);

    // 更新用户的贴子数
    void addOneArticleCountById(Long id);

    // 用户发帖数-1
    void subOneArticleCountById(Long id);

    // 修改用户信息
    void modifyInfo(User user);

    // 用户修改密码
    void modifyPassword (Long userId,String newPassword,String oldPassword);

    // 用户修改头像
    void modifyPicture(Long userId,String avatarUrl);

    // 获取所有用户列表
    List<User> getAllUsers();

    // 禁言用户
    void banUser(Long userId);

    // 解除禁言
    void unbanUser(Long userId);

    // 删除用户
    void deleteUser(Long userId);
}
