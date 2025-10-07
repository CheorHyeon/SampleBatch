package com.example.samplebatch.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import com.example.samplebatch.entity.BeforeEntity;

public class ExcelRowWriter implements ItemStreamWriter<BeforeEntity> {

	private final String filePath;
	private Workbook workbook;
	private Sheet sheet;
	private int currentRowNumber;
	private boolean isClosed;

	/**
	 * @param fileName 저장할 파일 이름 (예: "result.xlsx")
	 *                 실제 저장 경로는 프로젝트 루트의 ./data/{fileName} 으로 자동 지정
	 */
	public ExcelRowWriter(String fileName) {
		this.filePath = "./data/" + fileName;  // ✅ 자동으로 ./data/ 붙이기
		this.currentRowNumber = 0;
	}
	/** 해당 class 실행될 때 단 한번만 실행되는 메서드 -> 엑셀파일 생성, 기본적인 값 초기화 */
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {

		// ./data 디렉토리 자동 생성 (없으면)
		File parentDir = new File("./data");
		if (!parentDir.exists()) {
			try {
				Files.createDirectories(parentDir.toPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Sheet1");
	}

	/** chunk 단위 배치 수행 시 매 청크마다 한번씩 수행 -> 데이터가 write 될 수 있는 메서드 */
	@Override
	public void write(Chunk<? extends BeforeEntity> chunk) {
		for (BeforeEntity entity : chunk) {
			Row row = sheet.createRow(currentRowNumber++);
			row.createCell(0).setCellValue(entity.getUserName());
		}

	}

	/** 배치 처리되고 가장 마지막에 수행되는 메서드 -> 파일 닫거나 그동안 사용된 객체 변수 초기화 등 */
	@Override
	public void close() throws ItemStreamException {
		if (isClosed) return;

		try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
			workbook.write(fileOut);
		} catch (IOException e) {
			throw new ItemStreamException(e);
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				throw new ItemStreamException(e);
			} finally {
				isClosed = true;
			}
		}
	}
}
