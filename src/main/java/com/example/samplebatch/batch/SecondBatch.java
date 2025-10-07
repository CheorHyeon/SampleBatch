package com.example.samplebatch.batch;

import java.util.Collections;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.samplebatch.entity.WinEntity;
import com.example.samplebatch.repository.WinRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecondBatch {

	private final JobRepository jobRepository;
	// DataSource 설정에 명시적 지정 해둬서 없어도 되나 가독성 용이하기 위해 추가함
	@Qualifier("dataTransactionManager")
	private final PlatformTransactionManager platformTransactionManager;
	private final WinRepository winRepository;

	@Bean
	public Job secondJob() {

		return new JobBuilder("secondJob", jobRepository)
			.start(secondStep())
			.build();
	}

	@Bean
	public Step secondStep() {
		return new StepBuilder("secondStep", jobRepository)
			.<WinEntity, WinEntity>chunk(10, platformTransactionManager)
			.reader(winReader())
			.processor(trueProcessor())
			.writer(winWriter())
			.build();
	}

	@Bean
	public RepositoryItemReader<WinEntity> winReader() {
		return new RepositoryItemReaderBuilder<WinEntity>()
			.name("winReader")
			.pageSize(10)
			.methodName("findByWinGreaterThanEqual")
			.arguments(Collections.singletonList(10L))
			.repository(winRepository)
			.sorts(Map.of("id", Sort.Direction.ASC))  // 정렬 기준을 줘야 중복 없이 읽게끔
			.build();
	}

	@Bean
	public ItemProcessor<WinEntity, WinEntity> trueProcessor() {
		return item -> {
			item.setReward(true);
			return item;
		};
	}

	@Bean
	public RepositoryItemWriter<WinEntity> winWriter() {
		return new RepositoryItemWriterBuilder<WinEntity>()
			.repository(winRepository)
			.methodName("save")
			.build();
	}
}
