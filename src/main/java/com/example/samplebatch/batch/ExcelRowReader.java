package com.example.samplebatch.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * open, read, close, update 4개의 메소드를 구현해야 한다.
 */
public class ExcelRowReader implements ItemStreamReader<Row> {

	private final String filePath;
	private InputStream fileInputStream;
	private Workbook workbook;
	private Iterator<Row> rowCursor;
	private int currentRowNumber;
	// 배치 메타데이터에 어떤 행까지 수행했는지에 대해 저장할 key 용도
	private final String CURRENT_ROW_KEY = "current.row.number";

	public ExcelRowReader(String filePath) {
		this.filePath = filePath;
		this.currentRowNumber = 0;
	}

	/**
		ItemStreamReader가 실행됐을 때 단 한번만 실행
	 	- 엑셀 파일 열거나 특정 초기화 로직 내부 구현
	 */
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {

		try {
			// 🔽 resources 기준 상대경로
			Resource resource = new ClassPathResource(filePath);
			fileInputStream = resource.getInputStream();
			// 🔽 절대경로
			// fileInputStream = new FileInputStream(filePath);
			workbook = WorkbookFactory.create(fileInputStream);
			Sheet sheet = workbook.getSheetAt(0);
			this.rowCursor = sheet.iterator();

			// 동일 배치 파라미터에 대해 특정 키 값 "current.row.number"의 값이 존재한다면 초기화
			if (executionContext.containsKey(CURRENT_ROW_KEY)) {
				currentRowNumber = executionContext.getInt(CURRENT_ROW_KEY);
			}

			// 위의 값을 가져와 이미 실행한 부분은 건너 뜀
			for (int i = 0; i < currentRowNumber && rowCursor.hasNext(); i++) {
				rowCursor.next();
			}

		} catch (IOException e) {
			throw new ItemStreamException(e);
		}
	}

	/**
	 * 한번 열리고 난 뒤 배치 처리가 청크 단위 진행될 때 매번 호출되는 메서드
	   - 데이터 각 행을 읽도록 세팅
	 */
	@Override
	public Row read() {

		if (rowCursor != null && rowCursor.hasNext()) {
			currentRowNumber++;
			return rowCursor.next();
		} else {
			return null;
		}
	}

	/** read가 처리되고 나서 특정 변수값 update 등 세팅(매번 호출) */

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt(CURRENT_ROW_KEY, currentRowNumber);
	}

	/** 배치 작업이 모두 처리가 되고 한번 동작하는 메서드로 열린 파일 닫거나 변수 초기화 등 */
	@Override
	public void close() throws ItemStreamException {

		try {
			if (workbook != null) {
				workbook.close();
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		} catch (IOException e) {
			throw new ItemStreamException(e);
		}
	}
}
