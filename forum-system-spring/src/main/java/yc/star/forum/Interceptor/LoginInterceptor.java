package yc.star.forum.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import yc.star.forum.common.Constant;
import yc.star.forum.common.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Value("${forum.login.url}")
    private String loginUrl;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 检查请求头中是否有Authorization，有则使用Token认证
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token)) {
            // 这里简化处理，实际项目中应该验证token的有效性
            // 如果token有效，则允许通过
            return true;
        }
        
        // 2. 检查Session，兼容原有的Session认证方式
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(Constant.USER_SESSION) != null) {
            return true;
        }
        
        // 3. 判断是否是API请求还是页面请求
        String requestURI = request.getRequestURI();
        String requestWith = request.getHeader("X-Requested-With");
        
        // 如果是API请求，返回JSON格式的未授权信息
        if (requestURI.startsWith("/api") || "XMLHttpRequest".equals(requestWith)) {
            responseUnauthorized(response);
            return false;
        }
        
        // 4. 如果是页面请求，重定向到登录页
        // 校验URL是否正确
        if (!loginUrl.startsWith("/")) {
            loginUrl = "/" + loginUrl;
        }
        // 未登录跳转到登录页面
        response.sendRedirect(loginUrl);
        return false;
    }
    
    /**
     * 返回未授权的JSON响应
     */
    private void responseUnauthorized(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = response.getWriter();
        
        Result<Object> result = Result.fail(401, "未登录或登录已过期，请重新登录");
        out.write(objectMapper.writeValueAsString(result));
        out.flush();
        out.close();
    }
}