package com.example.samplebatch.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
// JPA를 사용할 때 DataSource, LocalContainerEntityManagerFactoryBean, PlatformTransactionManager
// 추가로 @EnableJpaRepositories 어노테이션 지정 필요
@EnableJpaRepositories(
	// 해당 Config가 어떤 패키지에 대해 동작할건지 지정
	basePackages = "com.example.samplebatch.repository",
	// 사용할 엔티티매니저, 트랜잭션매니저 메소드명 지정
	entityManagerFactoryRef = "dataEntityManager",
	transactionManagerRef = "dataTransactionManager"
)
public class DataDBConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDBSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	// 엔티티들을 관리하는 매니저
	public LocalContainerEntityManagerFactoryBean dataEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

		em.setDataSource(dataDBSource());
		// 엔티티들이 모여질 패키지들 지정 (해당 경로에 생기는 엔티티는 이 Config가 적용된다)
		em.setPackagesToScan(new String[] {"com.example.samplebatch.entity"});
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		// 2개의 DB 연결 시 ddl.auto 설정을 Config에서 해줘야 한다.
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.show_sql", "true");
		em.setJpaPropertyMap(properties);

		return em;
	}

	@Bean
	public PlatformTransactionManager dataTransactionManager() {

		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(dataEntityManager().getObject());
		return transactionManager;
	}
}
