package com.example.algoyai.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author JSW
 *
 * JacksonConfig 클래스는 Jackson 라이브러리를 사용하여 JSON 직렬화 및 역직렬화 시에
 * 적용할 설정을 구성하는 클래스입니다. Spring 컨텍스트에 ObjectMapper 빈을 등록합니다.
 */
@Configuration
public class JacksonConfig {

	/**
	 * ObjectMapper 빈을 생성하고 설정하는 메서드입니다.
	 *
	 * @return 설정된 ObjectMapper 객체를 반환합니다.
	 */
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		// JSON 파서에서 단일 인용부호를 허용하도록 설정
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

		// 역직렬화 시 알 수 없는 속성으로 인해 실패하지 않도록 설정
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper;
	}
}