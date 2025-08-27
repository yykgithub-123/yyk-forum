package yc.star.forum.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yc.star.forum.dao.UserMapper;
import yc.star.forum.model.User;
import yc.star.forum.service.UserService;
import yc.star.forum.utils.MD5Util;
import yc.star.forum.utils.UUIDUtil;

import java.util.Date;

/**
 * UserService实现类，基于原有的IUserService实现
 */
@Service
@Slf4j
public class UserServiceImpl2 implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User login(User user) {
        try {
            if (!StringUtils.hasLength(user.getUsername()) || !StringUtils.hasLength(user.getPassword())) {
                return null;
            }
            
            User dbUser = userMapper.selectByUserName(user.getUsername());
            if (dbUser == null) {
                return null;
            }
            
            String salt = dbUser.getSalt();
            if (!MD5Util.md5Salt(user.getPassword(), salt).equalsIgnoreCase(dbUser.getPassword())) {
                return null;
            }
            
            return dbUser;
        } catch (Exception e) {
            log.error("登录失败", e);
            return null;
        }
    }

    @Override
    public boolean register(User user) {
        try {
            // 参数校验
            if (user == null || !StringUtils.hasLength(user.getUsername()) || 
                    !StringUtils.hasLength(user.getPassword())) {
                return false;
            }
            
            // 判断用户是否存在
            if (isUsernameExist(user.getUsername())) {
                return false;
            }
            
            // 生成盐值
            String salt = UUIDUtil.uuidSalt();
            user.setSalt(salt);
            // 密码加密
            user.setPassword(MD5Util.md5Salt(user.getPassword(), salt));
            
            // 填充默认值
            if (user.getGender() == null) {
                user.setGender((byte) 0);
            }
            user.setAvatarUrl(null);
            user.setArticleCount(0);
            user.setState((byte) 0);
            user.setDeleteState((byte) 0);
            user.setIsAdmin((byte) 0);
            Date date = new Date();
            user.setCreateTime(date);
            user.setUpdateTime(date);
            
            int row = userMapper.insertSelective(user);
            return row == 1;
        } catch (Exception e) {
            log.error("注册失败", e);
            return false;
        }
    }

    @Override
    public boolean isUsernameExist(String username) {
        if (!StringUtils.hasLength(username)) {
            return false;
        }
        
        User user = userMapper.selectByUserName(username);
        return user != null;
    }

    @Override
    public User getUserInfo(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateUserInfo(User user) {
        try {
            if (user == null || user.getId() == null || user.getId() <= 0) {
                return false;
            }
            
            User existUser = userMapper.selectByPrimaryKey(user.getId());
            if (existUser == null) {
                return false;
            }
            
            // 如果要修改用户名，检查是否已存在
            if (StringUtils.hasLength(user.getUsername()) && 
                    !user.getUsername().equals(existUser.getUsername())) {
                User checkUser = userMapper.selectByUserName(user.getUsername());
                if (checkUser != null) {
                    return false;
                }
            }
            
            // 设置更新时间
            user.setUpdateTime(new Date());
            
            int row = userMapper.updateByPrimaryKeySelective(user);
            return row == 1;
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return false;
        }
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        try {
            if (userId == null || userId <= 0 || 
                    !StringUtils.hasLength(oldPassword) || 
                    !StringUtils.hasLength(newPassword)) {
                return false;
            }
            
            User user = userMapper.selectByPrimaryKey(userId);
            if (user == null) {
                return false;
            }
            
            // 验证旧密码
            String oldSecurity = MD5Util.md5Salt(oldPassword, user.getSalt());
            if (!oldSecurity.equalsIgnoreCase(user.getPassword())) {
                return false;
            }
            
            // 生成新的盐值和密码
            String salt = UUIDUtil.uuidSalt();
            String newSecurity = MD5Util.md5Salt(newPassword, salt);
            
            // 更新密码
            User updateUser = new User();
            updateUser.setId(userId);
            updateUser.setSalt(salt);
            updateUser.setPassword(newSecurity);
            updateUser.setUpdateTime(new Date());
            
            int row = userMapper.updateByPrimaryKeySelective(updateUser);
            return row == 1;
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return false;
        }
    }
} 