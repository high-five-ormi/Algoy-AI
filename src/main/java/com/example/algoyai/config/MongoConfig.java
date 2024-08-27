package com.example.algoyai.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUri;

	@Override
	protected String getDatabaseName() {
		return "mydatabase";
	}

	@Bean
	@Override
	public MongoClient mongoClient() {
		return MongoClients.create(mongoUri);
	}
}