package yc.star.forum.config;


import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * Swagger配置类
 */
// 配置类
@Configuration
// 开启Springfox-Swagger
@EnableOpenApi
public class SwaggerConfig {
/**
 * Springfox-Swagger基本配置
 */
    @Bean
    public Docket createApi() {
        Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("yc.star.forum.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
    // 配置API基本信息
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("论坛系统API")
                .description("论坛系统前后端分离API测试")
                .contact(new Contact("ycstar",
                        "http://127.0.0.1:8539/sign-in.html", "1571460232@qq.com"))
                .version("1.0")
                .build();
        return apiInfo;
    }
    /**
     * 解决SpringBoot 6.0以上与Swagger 3.0.0 不兼容的问题
     **/
    @Bean
    public WebMvcEndpointHandlerMapping
    webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
                                     ServletEndpointsSupplier servletEndpointsSupplier,
                                     ControllerEndpointsSupplier controllerEndpointsSupplier,
                                     EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
                                     WebEndpointProperties webEndpointProperties, Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
        Collection<ExposableWebEndpoint> webEndpoints =
                webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping =
                this.shouldRegisterLinksMapping(webEndpointProperties, environment,
                        basePath);
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints,
                endpointMediaTypes,
                corsProperties.toCorsConfiguration(), new
                EndpointLinksResolver(allEndpoints, basePath),
                shouldRegisterLinksMapping, null);
    }
    private boolean shouldRegisterLinksMapping(WebEndpointProperties
                                                       webEndpointProperties, Environment environment,
                                               String basePath) {
        return webEndpointProperties.getDiscovery().isEnabled() &&
                (StringUtils.hasText(basePath)
                        ||
                        ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
    }
}