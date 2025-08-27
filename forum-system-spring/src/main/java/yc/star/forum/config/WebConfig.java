package yc.star.forum.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import yc.star.forum.Interceptor.LoginInterceptor;

/**
 * 配置登录拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/sign-in.html"
                        ,"/sign-up.html"
                        ,"/user/login"
                        ,"/user/register"
                        ,"/user/logout"
                        ,"/article/list"
                        ,"/article/*/comments"
                        ,"/article/*"
                        ,"/swagger*/**"
                        ,"/v3*/**"
                        ,"/dist/**"
                        ,"/image/**"
                        ,"/**.ico"
                        ,"/js/**"); // 排除某些路径
    }
}