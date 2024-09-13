package com.example.algoyai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author JSW
 *
 * WebConfig 클래스는 Spring MVC 설정을 담당하며, 특히 CORS(Cross-Origin Resource Sharing) 설정을 구성합니다.
 * 이를 통해 프론트엔드 애플리케이션에서 백엔드 API에 접근할 수 있도록 허용합니다.
 */
@Configuration
public class WebConfig {

  @Value("${allowed-connection.url}")
  private String url;

  /**
   * CORS 설정을 위한 WebMvcConfigurer 빈을 생성하는 메서드입니다.
   * 이 메서드는 프론트엔드 애플리케이션이 백엔드 API에 대한 크로스 도메인 요청을 허용하도록 설정합니다.
   *
   * @return WebMvcConfigurer 객체를 반환합니다.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * CORS 설정을 구성하는 메서드입니다. "/**" 경로에 대해 지정된 프론트엔드 URL에서의 접근을 허용하며, GET, POST, PUT, DELETE,
       * OPTIONS 메서드를 사용할 수 있게 허용합니다. 또한, 모든 헤더와 자격 증명(Credentials)을 허용합니다.
       *
       * @param registry CORS 매핑을 등록할 CorsRegistry 객체
       */
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOrigins(url, "http://localhost:8081")
            .allowedMethods("GET", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true);
      }
    };
  }
}