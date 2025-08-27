package yc.star.forum.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.Constant;
import yc.star.forum.common.ResultCode;
import yc.star.forum.model.User;
import yc.star.forum.service.IUserService;
import yc.star.forum.utils.MD5Util;
import yc.star.forum.utils.UUIDUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Api(tags = "用户接口")
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.access-path}")
    private String accessPath;

    @Value("${file.max-size:5242880}") // 默认5MB
    private long maxFileSize;
    
    @Value("${file.allowed-types:image/jpeg,image/png,image/gif}")
    private List<String> allowedTypes;

    @Resource
    private IUserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public AppResult register(@ApiParam(value = "⽤⼾名") @RequestParam("username") @NonNull String username,
                              @ApiParam(value = "昵称") @RequestParam("nickname") @NonNull String nickname,
                              @ApiParam(value = "密码") @RequestParam("password") @NonNull String password,
                              @ApiParam(value = "确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat) {
        // 校验密码是否一样
        if (!password.equals(passwordRepeat)) {
            log.info(ResultCode.FAILED_TWO_PWD_NOT_SAME.toString());
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }

        // 准备数据
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        // 生成安全密码
        String salt = UUIDUtil.uuidSalt();
        String encryptPassword = MD5Util.md5Salt(password,salt);
        user.setSalt(salt);
        user.setPassword(encryptPassword);

        userService.createNormalUser(user);

        return AppResult.success();
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AppResult lgoin(@ApiParam(value = "⽤⼾名") @RequestParam("username") @NonNull String username
            , @ApiParam(value = "密码") @RequestParam("password") @NonNull String password
            ,HttpServletRequest request) {
        // 准备数据
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        // 验证登录
        User realUser = userService.verifyLogin(user);

        // 用户信息存入session
        HttpSession session = request.getSession(true);
        session.setAttribute(Constant.USER_SESSION,realUser);

        // 返回用户类型
        return AppResult.success(realUser.getIsAdmin() == 1 ? "admin" : "user");
    }

    @ApiOperation("获取用户信息")
    @GetMapping("info")
    public AppResult getUserInfo(@ApiParam(value = "用户id") Long id
            ,HttpServletRequest request) {
        Long userId = id;
        if (id == null) {
            // 获取session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute(Constant.USER_SESSION) == null) {
                return AppResult.failed(ResultCode.FAILED_LOGIN);
            }
            // 从session中获取用户信息
            User user = (User) session.getAttribute(Constant.USER_SESSION);
            userId = user.getId();
        }
        // 查询用户信息
        User user = userService.selectUserInfo(userId);
        return AppResult.success(user);
    }

    @ApiOperation("用户注销")
    @GetMapping("logout")
    public AppResult logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("退出成功");
            // 销毁session
            session.invalidate();
        }
        return AppResult.success("退出成功");
    }

    @ApiOperation("用户修改信息")
    @PostMapping("/modifyInfo")
    public AppResult<User> modifyInfo (HttpServletRequest request
            ,@ApiParam("用户名") @RequestParam(value = "username",required = false) String username
            ,@ApiParam("用户昵称") @RequestParam(value = "nickname",required = false) String nickname
            ,@ApiParam("性别") @RequestParam(value = "gender",required = false) Byte gender
            ,@ApiParam("电子邮件") @RequestParam(value = "email",required = false) String email
            ,@ApiParam("电话号码") @RequestParam(value = "phoneNum",required = false) String phoneNum
            ,@ApiParam("个人简介") @RequestParam(value = "remark",required = false) String remark) {
        if (!StringUtils.hasLength(username)
                && !StringUtils.hasLength(nickname)
                && gender == null
                && !StringUtils.hasLength(email)
                && !StringUtils.hasLength(phoneNum)
                && !StringUtils.hasLength(remark)) {
            return AppResult.failed("请输入要修改的内容");
        }
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        // 封装更新对象
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(username);
        updateUser.setNickname(nickname);
        updateUser.setGender(gender);
        updateUser.setEmail(email);
        updateUser.setPhoneNum(phoneNum);
        updateUser.setRemark(remark);
        // 调用业务层更新对象
        userService.modifyInfo(updateUser);
        // 更新session中的对象
        user = userService.selectUserInfo(user.getId());
        session.setAttribute(Constant.USER_SESSION,user);
        return AppResult.success(user);
    }

    @ApiOperation("用户修改密码")
    @PostMapping("/modifyPwd")
    public AppResult modifyPassword (HttpServletRequest request
            , @ApiParam("原密码") @RequestParam("oldPassword") @NonNull String oldPassword
            , @ApiParam("新密码") @RequestParam("newPassword") @NonNull String newPassword
            , @ApiParam("确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat) {
        // 校验新密码和确认密码是否相同
        if (!newPassword.equals(passwordRepeat)) {
            log.info("新密码和确认密码不相同");
            return AppResult.failed("新密码和确认密码不相同，请重新输入密码");
        }
        // 从session中获取用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        // 调用业务层修改密码
        userService.modifyPassword(user.getId(),newPassword,oldPassword);
        // 销毁session重新登录
        if (session != null) {
            session.invalidate();
        }
        return AppResult.success();
    }


    @ApiOperation("修改用户头像")
    @PostMapping("/picture")
    public AppResult<User> modifyPicture(HttpServletRequest request,
            @ApiParam("头像文件") @RequestParam("file") MultipartFile file) {
        // 1. 基础验证
        if (file.isEmpty()) {
            return AppResult.failed("请选择要上传的头像文件");
        }
        
        // 2. 文件大小验证
        if (file.getSize() > maxFileSize) {
            return AppResult.failed("文件大小超过限制");
        }
        
        // 3. 文件类型验证
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            return AppResult.failed("不支持的文件类型，请上传jpg、png或gif格式的图片");
        }

        // 4. 获取session用户
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        User nowUser = (User) session.getAttribute(Constant.USER_SESSION);

        try {
            // 5. 生成安全的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);
            String newFileName = UUIDUtil.uuid() + "." + fileExtension;
            
            // 6. 确保上传目录存在
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists() && !uploadPath.mkdirs()) {
                log.error("无法创建上传目录: {}", uploadPath.getAbsolutePath());
                return AppResult.failed("服务器存储错误");
            }

            // 7. 保存文件
            File destFile = new File(uploadPath, newFileName);
            file.transferTo(destFile);
            
            // 8. 更新用户头像信息 (使用访问路径)
            String avatarUrl = accessPath + newFileName;
            userService.modifyPicture(nowUser.getId(), avatarUrl);
            
            // 9. 更新session中的用户信息
            User updatedUser = userService.selectUserInfo(nowUser.getId());
            session.setAttribute(Constant.USER_SESSION, updatedUser);
            
            // 10. 返回结果
            User user = new User();
            user.setAvatarUrl(avatarUrl);
            return AppResult.success(user);

        } catch (IOException e) {
            log.error("头像上传失败", e);
            return AppResult.failed("头像上传失败，请稍后重试");
        }
    }

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public AppResult getUserList(HttpServletRequest request) {
        // 检查是否是管理员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        User currentUser = (User) session.getAttribute(Constant.USER_SESSION);
        if (currentUser.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_PERMISSION);
        }

        List<User> users = userService.getAllUsers();
        return AppResult.success(users);
    }

    @ApiOperation("禁言用户")
    @PostMapping("/ban")
    public AppResult banUser(@ApiParam(value = "用户ID") @RequestParam("id") Long id,
                           HttpServletRequest request) {
        // 检查是否是管理员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        User currentUser = (User) session.getAttribute(Constant.USER_SESSION);
        if (currentUser.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_PERMISSION);
        }

        userService.banUser(id);
        return AppResult.success();
    }

    @ApiOperation("解除禁言")
    @PostMapping("/unban")
    public AppResult unbanUser(@ApiParam(value = "用户ID") @RequestParam("id") Long id,
                             HttpServletRequest request) {
        // 检查是否是管理员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        User currentUser = (User) session.getAttribute(Constant.USER_SESSION);
        if (currentUser.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_PERMISSION);
        }

        userService.unbanUser(id);
        return AppResult.success();
    }

    @ApiOperation("删除用户")
    @PostMapping("/delete")
    public AppResult deleteUser(@ApiParam(value = "用户ID") @RequestParam("id") Long id,
                              HttpServletRequest request) {
        // 检查是否是管理员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        User currentUser = (User) session.getAttribute(Constant.USER_SESSION);
        if (currentUser.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_PERMISSION);
        }

        userService.deleteUser(id);
        return AppResult.success();
    }
}