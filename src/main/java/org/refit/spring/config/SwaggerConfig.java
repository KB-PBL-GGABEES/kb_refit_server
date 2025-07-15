package org.refit.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private final String API_NAME = "Re:fit API";
    private final String API_VERSION = "1.0";
    private final String API_DESCRIPTION = "Re:fit API";

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_SCOPE = "global";
    private static final String AUTH_DESCRIPTION = "accessEverything";

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)
                .description(API_DESCRIPTION)
                .version(API_VERSION)
                .build();
    }
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(List.of(new ApiKey("Authorization", "Authorization", "header")))
                .securityContexts(List.of(SecurityContext.builder()
                        .securityReferences(List.of(
                                new SecurityReference("Authorization", new AuthorizationScope[]{ new AuthorizationScope("global", "accessEverything") })
                        ))
                        .build()));
    }
    private ApiKey apiKey() {
        return new ApiKey(AUTH_HEADER, AUTH_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope scope = new AuthorizationScope(AUTH_SCOPE, AUTH_DESCRIPTION);
        return List.of(new SecurityReference(AUTH_HEADER, new AuthorizationScope[]{scope}));
    }
}
