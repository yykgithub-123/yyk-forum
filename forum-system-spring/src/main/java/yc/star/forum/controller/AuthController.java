package yc.star.forum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import yc.star.forum.common.Constant;
import yc.star.forum.common.Result;
import yc.star.forum.model.User;
import yc.star.forum.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户认证相关API
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user, HttpServletRequest request) {
        // 验证用户名和密码
        User loginUser = userService.login(user);
        if (loginUser != null) {
            // 生成token (简化处理，实际项目应该使用JWT等方案)
            String token = UUID.randomUUID().toString().replace("-", "");
            
            // 将用户信息存入session (兼容原有逻辑)
            HttpSession session = request.getSession(true);
            session.setAttribute(Constant.USER_SESSION, loginUser);
            
            // 返回用户信息和token
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", loginUser.getId());
            data.put("username", loginUser.getUsername());
            
            return Result.success(data, "登录成功");
        } else {
            return Result.fail(401, "用户名或密码错误");
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userService.isUsernameExist(user.getUsername())) {
            return Result.fail("用户名已存在");
        }
        
        // 注册用户
        boolean success = userService.register(user);
        if (success) {
            return Result.success(null, "注册成功");
        } else {
            return Result.fail("注册失败");
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        // 从session中获取用户信息
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute(Constant.USER_SESSION);
            if (user != null) {
                // 敏感信息置空
                user.setPassword(null);
                return Result.success(user);
            }
        }
        return Result.fail(401, "未登录或登录已过期");
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Result.success(null, "退出成功");
    }
} 