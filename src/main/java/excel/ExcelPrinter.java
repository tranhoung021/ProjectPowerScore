package excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelPrinter {

	private XSSFWorkbook workbook;
	private String excelName;

	public ExcelPrinter(String name) throws IOException {
		workbook = loadWorkbook();
		excelName = name;
		System.out.println("Skapad");
	}

	private XSSFWorkbook loadWorkbook() throws IOException {
		File file = new File("C:/Eclipse/resultat_" + excelName + ".xlsx");
		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				return new XSSFWorkbook(fis);
			}
		} else {
			return new XSSFWorkbook();
		}
	}

	public void add(Object[][] data, String sheetName) {
		XSSFSheet sheet = workbook.getSheet(sheetName);

		if (sheet == null) {
			sheet = workbook.createSheet(sheetName);
			// Add headers to the first row
			XSSFRow headerRow = sheet.createRow(0);
			for (int i = 0; i < data[0].length; i++) {
				XSSFCell cell = headerRow.createCell(i);
				cell.setCellValue(String.valueOf(data[0][i]));
			}
		}

		int lastRowNum = sheet.getLastRowNum();
		XSSFRow lastRow = null;
		if (lastRowNum > 0) {
			lastRow = sheet.getRow(lastRowNum);
			while (lastRow != null && lastRow.getCell(0) == null) {
				lastRow = sheet.getRow(lastRowNum - 1);
				lastRowNum--;
			}
		}

		// Add new data to the existing sheet
		int rowCount = lastRow == null ? 0 : lastRow.getRowNum() + 1;
		for (int i = 0; i < data.length; i++) {
			XSSFRow row = sheet.createRow(rowCount + i);
			for (int j = 0; j < data[i].length; j++) {
				XSSFCell cell = row.createCell(j);
				Object value = data[i][j];
				if (value == null) {
					cell.setCellValue(""); // Set an empty string instead of null
				} else {
					cell.setCellValue(String.valueOf(value));
				}
			}
		}
	}

	public void write(File file) throws IOException {

		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				XSSFWorkbook existingWorkbook = new XSSFWorkbook(fis);
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
					XSSFSheet newSheet = workbook.getSheetAt(i);
					XSSFSheet existingSheet = existingWorkbook.getSheet(newSheet.getSheetName());
					if (existingSheet != null) {
						// Update existing sheet
						int lastRow = existingSheet.getLastRowNum();
						if (lastRow == 0) {
							// Add headers if the existing sheet is empty
							XSSFRow headerRow = newSheet.getRow(0);
							XSSFRow existingHeaderRow = existingSheet.createRow(0);
							for (int columnIndex = 0; columnIndex < headerRow.getLastCellNum(); columnIndex++) {
								XSSFCell newCell = headerRow.getCell(columnIndex);
								XSSFCell existingCell = existingHeaderRow.createCell(columnIndex);
								existingCell.setCellValue(newCell.getStringCellValue());
							}
						}
						// Shift existing data down to make room for new data
						for (int rowIndex = lastRow; rowIndex >= 1; rowIndex--) {
							XSSFRow existingRow = existingSheet.getRow(rowIndex - 1);
							if (existingRow != null) {
								XSSFRow newRow = existingSheet.createRow(rowIndex);
								for (int columnIndex = 0; columnIndex < existingRow.getLastCellNum(); columnIndex++) {
									XSSFCell existingCell = existingRow.getCell(columnIndex);
									XSSFCell newCell = newRow.createCell(columnIndex);
									newCell.setCellValue(existingCell.getStringCellValue());
								}
							}
						}
						// Add new competitor data
						for (int rowIndex = 1; rowIndex <= newSheet.getLastRowNum(); rowIndex++) {
							XSSFRow newRow = newSheet.getRow(rowIndex);
							XSSFRow existingRow = existingSheet.getRow(rowIndex);
							if (existingRow == null) {
								existingRow = existingSheet.createRow(rowIndex);
							}
							for (int columnIndex = 0; columnIndex < newRow.getLastCellNum(); columnIndex++) {
								XSSFCell newCell = newRow.getCell(columnIndex);
								XSSFCell existingCell = existingRow.getCell(columnIndex);
								if (existingCell == null) {
									existingCell = existingRow.createCell(columnIndex);
								}
								existingCell.setCellValue(newCell.getStringCellValue());
							}
						}
					} else {
						// Create new sheet if it doesn't exist
						existingSheet = existingWorkbook.createSheet(newSheet.getSheetName());
						// Add headers
						XSSFRow headerRow = newSheet.getRow(0);
						XSSFRow existingHeaderRow = existingSheet.createRow(0);
						for (int columnIndex = 0; columnIndex < headerRow.getLastCellNum(); columnIndex++) {
							XSSFCell newCell = headerRow.getCell(columnIndex);
							XSSFCell existingCell = existingHeaderRow.createCell(columnIndex);
							existingCell.setCellValue(newCell.getStringCellValue());
						}
						// Add new competitor data
						for (int rowIndex = 1; rowIndex <= newSheet.getLastRowNum(); rowIndex++) {
							XSSFRow newRow = newSheet.getRow(rowIndex);
							XSSFRow existingRow = existingSheet.createRow(rowIndex);
							for (int columnIndex = 0; columnIndex < newRow.getLastCellNum(); columnIndex++) {
								XSSFCell newCell = newRow.getCell(columnIndex);
								XSSFCell existingCell = existingRow.createCell(columnIndex);
								existingCell.setCellValue(newCell.getStringCellValue());
							}
						}
					}
				}
				try (FileOutputStream out = new FileOutputStream(file)) {
					existingWorkbook.write(out);
				}
			}
		} else {
			try (FileOutputStream fos = new FileOutputStream(file)) {
				workbook.write(fos);
			}
		}
	}


			private int findFirstEmptyRow(XSSFSheet sheet) {
		int lastRowNum = sheet.getLastRowNum();
		for (int i = 0; i <= lastRowNum; i++) {
			XSSFRow row = sheet.getRow(i);
			if (row == null || isEmptyRow(row)) {
				return i;
			}
		}
		return lastRowNum + 1;
	}

	private boolean isEmptyRow(XSSFRow row) {
		for (int i = 0; i < row.getLastCellNum(); i++) {
			XSSFCell cell = row.getCell(i);
			if (cell != null && !cell.toString().trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}



}