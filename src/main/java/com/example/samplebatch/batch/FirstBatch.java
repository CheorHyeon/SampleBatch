package com.example.samplebatch.batch;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.samplebatch.entity.AfterEntity;
import com.example.samplebatch.entity.BeforeEntity;
import com.example.samplebatch.repository.AfterRepository;
import com.example.samplebatch.repository.BeforeRepository;

@Configuration
public class FirstBatch {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;

	private final BeforeRepository beforeRepository;
	private final AfterRepository afterRepository;

	public FirstBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
		BeforeRepository beforeRepository, AfterRepository afterRepository) {
		this.jobRepository = jobRepository;
		this.platformTransactionManager = platformTransactionManager;
		this.beforeRepository = beforeRepository;
		this.afterRepository = afterRepository;
	}

	@Bean
	public Job firstJob() {
		System.out.println("first job");

		// Job에 대한 이름, 해당 작업에 대한 트래킹을 위한 jobRepository
		return new JobBuilder("firstJob", jobRepository)
			// 1개일 때 start, 2개 이상일 때 next 메소드
			.start(firstStep())
			// .next()
			.build();
	}

	@Bean
	public Step firstStep() {

		// 이름, 트래킹할 JobRepository
		return new StepBuilder("firstStep", jobRepository)
			// chunk size : 10, 한 트랜잭션 단위에서 다룰 개수 (pageSize와 안맞더라도 트랜잭션 단위를 청크 사이즈로 맞춤)
			// 아래 데이터 타입은 <Read에서 쓸 데이터타입, Writer 에서 쓸 데이터타입> 으로 정의
			// platformTransactionManager : chunk 진행 실패 시 Rollback 진행 및 다시 처리 세팅
			.<BeforeEntity, AfterEntity> chunk(10, platformTransactionManager)
			.reader(beforeReader())
			.processor(middleProcessor())
			.writer(afterWriter())
			.build();
	}

	// read : Bean 등록
	@Bean
	public RepositoryItemReader<BeforeEntity> beforeReader() {
		return new RepositoryItemReaderBuilder<BeforeEntity>()
			.name("beforeReader")
			.pageSize(10)  // 한번에 가져올 페이지 사이즈 지정
			.methodName("findAll")  // JPA Method 지정 가능
			.repository(beforeRepository)  // 메소드 호출할 repository 지정
			.sorts(Map.of("id", Sort.Direction.ASC))
			.build();
	}

	// Process : 읽어온 데이터를 처리하는 Process (큰 작업을 수행하지 않을 경우 생략 가능, 단순 이동 사실 필없)
	@Bean
	public ItemProcessor<BeforeEntity, AfterEntity> middleProcessor() {

		return new ItemProcessor<BeforeEntity, AfterEntity>() {

			@Override
			public AfterEntity process(BeforeEntity item) {

				AfterEntity afterEntity = new AfterEntity();
				afterEntity.setUsername(item.getUserName());

				return afterEntity;
			}
		};
	}

	@Bean
	public RepositoryItemWriter<AfterEntity> afterWriter() {

		return new RepositoryItemWriterBuilder<AfterEntity>()
			.repository(afterRepository)  // 리포지토리 지정
			.methodName("save")  // 메소드명 지정
			.build();
	}


}
