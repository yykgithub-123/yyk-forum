package yc.star.forum.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.ResultCode;
import yc.star.forum.dao.UserMapper;
import yc.star.forum.exception.ApplicationException;
import yc.star.forum.model.User;
import yc.star.forum.service.IUserService;
import yc.star.forum.utils.MD5Util;
import yc.star.forum.utils.UUIDUtil;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public void createNormalUser(User user) {
        // 参数校验
        if (user == null || !StringUtils.hasLength(user.getUsername()) ||
                !StringUtils.hasLength(user.getPassword()) ||
                !StringUtils.hasLength(user.getSalt())) {
            // 打印日志
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        // 判断用户是否存在
        User user1 = userMapper.selectByUserName(user.getUsername());
        if (user1 != null) {
            log.info(ResultCode.AILED_USER_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.AILED_USER_EXISTS));
        }

        // 除了注册时传⼊的有效值，其他全部使⽤默认
        if (user.getGender() == null) {
            // 性别赋默认值
            user.setGender((byte) 0);
        }
        // 填充默认值
        user.setAvatarUrl(null);
        user.setArticleCount(0);
        user.setState((byte) 0);
        user.setDeleteState((byte) 0);
        user.setIsAdmin((byte) 0);
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);
        int row = userMapper.insertSelective(user);
        if (row != 1) {
            log.error(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        log.info("新增成功");
    }

    @Override
    public User verifyLogin(User user) {
        if (!StringUtils.hasLength(user.getUsername()) || !StringUtils.hasLength(user.getPassword())) {
            // 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        User user1 = userMapper.selectByUserName(user.getUsername());
        if (user1 == null) {
            // 用户不存在
            log.info(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        String salt = user1.getSalt();
        if (!MD5Util.md5Salt(user.getPassword(),salt).equalsIgnoreCase(user1.getPassword())) {
            // 密码错误
            log.warn(ResultCode.FAILED_LOGIN.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        log.info("登录成功");
        return user1;
    }

    @Override
    public User selectUserInfo(Long id) {
        if (id == null || id <= 0) {
            // 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            // 用户不存在
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        return user;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        // 参数校验
        if (id == null) {
            // 板块更新帖子数量失败
            log.warn(ResultCode.FAILED_BOARD_ALTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ALTICLE_COUNT));
        }
        User user = userMapper.selectByPrimaryKey(id);
        // 用户不存在
        if (user == null) {
            log.warn("用户不存在");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        // 更新用户帖子数量
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setArticleCount(user.getArticleCount()+1);
        // 执行数据库操作
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.error("受影响行数：" + row);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void subOneArticleCountById(Long id) {
        // 参数校验
        if (id == null) {
            // 板块更新帖子数量失败
            log.warn(ResultCode.FAILED_BOARD_ALTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ALTICLE_COUNT));
        }
        User user = userMapper.selectByPrimaryKey(id);
        // 用户不存在
        if (user == null) {
            log.warn("用户不存在");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        // 更新用户帖子数量
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setArticleCount(user.getArticleCount()-1);
        // 如果帖子数量小于零，设置成零
        if (updateUser.getArticleCount() < 0) {
            updateUser.setArticleCount(0);
        }
        // 执行数据库操作
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.error("受影响行数：" + row);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyInfo(User user) {
        // 校验参数的有效性
        if (user == null || user.getId() == null || user.getId() <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 根据用户id查找出用户信息
        User existUser = userMapper.selectByPrimaryKey(user.getId());
        if (existUser == null) {
            log.warn("用户不存在");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        // 定义一个标志位
        boolean checkAttr = false; // false 表示参数不合格，不能执行更新操作

        // 定义一个更新对象，用来执行更新操作
        User updateUser = new User();
        // 设置用户id值
        updateUser.setId(user.getId());

        // 1. 校验用户名的合理性
        if (StringUtils.hasLength(user.getUsername()) && !user.getUsername().equals(existUser.getUsername())) {
            // 判断数据库中有没有该用户名的用户
            User checkUser = userMapper.selectByUserName(user.getUsername());
            if (checkUser != null) {
                log.info("该用户名已存在，不能修改");
                throw new ApplicationException(AppResult.failed(ResultCode.AILED_USERNAME_EXISTS));
            }
            // 用户名不存在。可以修改
            updateUser.setUsername(user.getUsername());
            // 设置标志位
            checkAttr = true;
        }

        // 2.校验昵称
        if (StringUtils.hasLength(user.getNickname()) && !user.getNickname().equals(existUser.getNickname())) {
            // 校验成功，可以更新
            updateUser.setNickname(user.getNickname());
            // 修改标志位
            checkAttr = true;
        }

        // 3.校验性别
        if (user.getGender() != null && user.getGender() != existUser.getGender()) {
            if (user.getGender() > 2 || user.getGender() < 0) {
                user.setGender((byte) 2);
            }
            // 校验成功，可以更新
            updateUser.setGender(user.getGender());
            // 修改标志位
            checkAttr = true;
        }

        // 4. 校验邮箱
        if (StringUtils.hasLength(user.getEmail()) && !user.getEmail().equals(existUser.getEmail())) {
            if (!user.getEmail().endsWith("@qq.com")) {
                throw new ApplicationException(AppResult.failed("电子邮件格式不合法，请加上@qq.com"));
            }
            // 校验成功，可以更新
            updateUser.setEmail(user.getEmail());
            // 修改标志位
            checkAttr = true;
        }

        // 5. 校验电话
        if (StringUtils.hasLength(user.getPhoneNum()) && !user.getPhoneNum().equals(existUser.getPhoneNum())) {
            if (user.getPhoneNum().length() != 11) {
                throw new ApplicationException(AppResult.failed("电话长度不合法"));
            }
            // 校验成功，可以更新
            updateUser.setPhoneNum(user.getPhoneNum());
            // 修改标志位
            checkAttr = true;
        }

        // 5. 校验个人简介
        if (StringUtils.hasLength(user.getRemark()) && !user.getRemark().equals(existUser.getRemark())) {
            // 校验成功，可以更新
            updateUser.setRemark(user.getRemark());
            // 修改标志位
            checkAttr = true;
        }

        if (checkAttr == false) {
            log.info("参数校验不成功，无法进行修改");
            throw new ApplicationException(AppResult.failed("参数校验不成功，无法进行修改"));
        }
        // 参数校验成功
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.error("执行更新的sql出问题");
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void modifyPassword(Long userId, String newPassword, String oldPassword) {
        // 校验参数
        if (userId == null || userId <= 0
                || !StringUtils.hasLength(newPassword) || !StringUtils.hasLength(oldPassword)) {
            log.info("参数校验不成功，无法进行修改");
            throw new ApplicationException(AppResult.failed("参数校验不成功，无法进行修改"));
        }
        // 校验当前用户的有效性
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null || user.getDeleteState() == 1) {
            log.warn("该用户不存在");
            throw new ApplicationException(AppResult.failed("该用户已被删除或不存在"));
        }
        // 对输入的老密码进行校验
        String oldSecurity = MD5Util.md5Salt(oldPassword, user.getSalt());
        if (!oldSecurity.equalsIgnoreCase(user.getPassword())) {
            log.info("输入的老密码不正确");
            throw new ApplicationException(AppResult.failed("原密码不正确，请输入正确的密码"));
        }
        // 原密码校验成功
        String salt = UUIDUtil.uuidSalt(); // 生成新的盐值
        // 对新密码加密
        String newSecurity = MD5Util.md5Salt(newPassword, salt);
        // 准备数据
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setSalt(salt);
        updateUser.setPassword(newSecurity);
        Date date = new Date();
        updateUser.setUpdateTime(date);

        // 调用数据层，执行修改密码
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            throw new ApplicationException(AppResult.failed("服务器内部错误"));
        }

    }

    @Override
    public void modifyPicture(Long userId,String avatarUrl) {
        // 校验参数
        if (!StringUtils.hasLength(avatarUrl)) {
            log.info("参数校验不成功，无法进行修改");
            throw new ApplicationException(AppResult.failed("参数校验不成功，无法进行修改"));
        }
        User existUser = userMapper.selectByPrimaryKey(userId);
        if (existUser == null || existUser.getDeleteState() == 1) {
            throw new ApplicationException(AppResult.failed("该用户不存在"));
        }
        // 构造数据
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setAvatarUrl(avatarUrl);

        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            throw new ApplicationException(AppResult.failed("服务器内部错误，头像更新失败"));
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    @Override
    public void banUser(Long userId) {
        if (userId == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        // 设置用户状态为禁言(1)
        user.setState((byte) 1);
        user.setUpdateTime(new Date());
        
        int row = userMapper.updateByPrimaryKeySelective(user);
        if (row != 1) {
            log.error("禁言用户失败");
            throw new ApplicationException(AppResult.failed("禁言用户失败"));
        }
        log.info("禁言用户成功");
    }

    @Override
    public void unbanUser(Long userId) {
        if (userId == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        // 设置用户状态为正常(0)
        user.setState((byte) 0);
        user.setUpdateTime(new Date());
        
        int row = userMapper.updateByPrimaryKeySelective(user);
        if (row != 1) {
            log.error("解除禁言失败");
            throw new ApplicationException(AppResult.failed("解除禁言失败"));
        }
        log.info("解除禁言成功");
    }

    @Override
    public void deleteUser(Long userId) {
        if (userId == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        // 设置删除状态为已删除(1)
        user.setDeleteState((byte) 1);
        user.setUpdateTime(new Date());
        
        int row = userMapper.updateByPrimaryKeySelective(user);
        if (row != 1) {
            log.error("删除用户失败");
            throw new ApplicationException(AppResult.failed("删除用户失败"));
        }
        log.info("删除用户成功");
    }
}