package com.example.samplebatch.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MetaDBConfig {

	@Primary // 동일한 Type (Package 경로 + Class명)의 Bean은 1개만 존재해야 하기에 기본값으론 Meta 써라
	@Bean
	// properties에 있는 변수 설정 값을 자동으로 불러오는 어노테이션
	@ConfigurationProperties(prefix = "spring.datasource-meta")
	public DataSource metaDBSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean
	public PlatformTransactionManager metaTransactionManager() {
		return new DataSourceTransactionManager(metaDBSource());
	}
}
