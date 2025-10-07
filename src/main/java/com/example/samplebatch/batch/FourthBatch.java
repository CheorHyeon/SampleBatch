package com.example.samplebatch.batch;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.samplebatch.entity.AfterEntity;
import com.example.samplebatch.repository.AfterRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FourthBatch {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final AfterRepository afterRepository;

	@Bean
	public Job fourthJob() {
		return new JobBuilder("forthJob", jobRepository)
			.start(fourthStep())
			.build();
	}

	@Bean
	public Step fourthStep() {
		return new StepBuilder("fourthStep", jobRepository)
			.<Row, AfterEntity>chunk(10, platformTransactionManager)
			.reader(excelReader())
			.processor(fourthProcessor())
			.writer(fourthAfterWriter())
			.build();
	}

	@Bean
	public ItemReader<Row> excelReader() {
		try {
			return new ExcelRowReader("dummy.xlsx");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public ItemProcessor<Row, AfterEntity> fourthProcessor() {
		return item -> {
			AfterEntity afterEntity = new AfterEntity();
			afterEntity.setUsername(item.getCell(0).getStringCellValue());
			return afterEntity;
		};
	}

	@Bean
	public RepositoryItemWriter<AfterEntity> fourthAfterWriter() {
		return new RepositoryItemWriterBuilder<AfterEntity>()
			.repository(afterRepository)
			.methodName("save")
			.build();
	}

}
