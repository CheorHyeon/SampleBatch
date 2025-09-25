package com.example.samplebatch.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FirstSchedule {

	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;

	//@Scheduled(cron = "10 * * * * *", zone = "Asia/Seoul")
	public void runFirstJob() throws Exception {

		System.out.println("first schedule start");

		// 시간까지 가면 하루에 한번만 한다던가 하는 배치 작업 방지를 불가
		// Job Parameter를 잘 구성해야 함
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		String date = dateFormat.format(new Date());

		JobParameters jobParameters = new JobParametersBuilder()
			.addString("date", date)
			.toJobParameters();

		jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);

	}
}
