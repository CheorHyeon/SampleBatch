package com.example.samplebatch.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MainController {

	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;

	@GetMapping("/first")
	// 배치 작업 중 예외 발생 처리 할 수 있도록 던져줘야 함
	public String firstApi(@RequestParam("value") String value) throws Exception {

		// 겹치는 일자가 들어오면 실행시키지 않게 하기 위해 값을 줌
		JobParameters jobParameters = new JobParametersBuilder()
			.addString("date", value)
			.toJobParameters();

		// 비동기로 실행해보면 좋으나, 배치 작업이 실행 되는지 봐야하니 동기로
		jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);

		return "ok";
	}
}
