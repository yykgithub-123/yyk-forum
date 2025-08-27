package yc.star.forum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许跨域的头部信息
        config.addAllowedHeader("*");
        // 允许跨域的方法
        config.addAllowedMethod("*");
        // 允许跨域的源
        config.addAllowedOrigin("*");
        // 允许携带cookie信息
        config.setAllowCredentials(true);
        // 暴露头部信息
        config.addExposedHeader("Authorization");
        
        // 对所有接口都有效
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
} 