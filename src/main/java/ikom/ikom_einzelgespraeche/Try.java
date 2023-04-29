package ikom.ikom_einzelgespraeche;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Try {

	// any exceptions need to be caught
	public static void main(String[] args) throws Exception {
		write_correct();
	}

	static void write_correct() throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("sheet1");

		XSSFRow row = sheet.createRow(1);

		// Background color
		XSSFCellStyle style = workbook.createCellStyle();
		style.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
		style.setFillPattern(FillPatternType.DIAMONDS);

		XSSFCell cell = row.createCell(1);
		cell.setCellValue("welcome");
		cell.setCellStyle(style);

		// foreground color
		style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.fromInt(1).getIndex());
		style.setFillPattern(FillPatternType.FINE_DOTS);

		cell = row.createCell(2);
		cell.setCellValue("Geeks");
		cell.setCellStyle(style);

		FileOutputStream file = new FileOutputStream(
				new File(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "plan.xlsx"));
		workbook.write(file);
		file.close();
		System.out.println("Style Created");

	}

	static void write_try() throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet spreadsheet = workbook.createSheet(" Data ");

		// This data needs to be written (Object[])
		ArrayList<ArrayList<String>> data = new ArrayList<>();

		ArrayList<String> dataRow1 = new ArrayList<>();
		dataRow1.add("Anna Doan");
		dataRow1.add("Company A");
		dataRow1.add("Beta Doan");
		dataRow1.add("Company B");
		data.add(dataRow1);
		ArrayList<String> dataRow2 = new ArrayList<>();
		dataRow2.add("Delta Doan");
		dataRow2.add("Company C");
		data.add(dataRow2);

		XSSFRow row;
		int rowid = 0;
		for (ArrayList<String> objectArr : data) {
			row = spreadsheet.createRow(rowid++);
			int cellid = 0;
			for (String obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue(obj);
			}
		}

		// .xlsx is the format for Excel Sheets...
		// writing the workbook into the file...
		FileOutputStream out = new FileOutputStream(
				new File(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "plan.xlsx"));

		workbook.write(out);
		out.close();
	}

	void write_plan_to_excel(List<List<Pair<Company, Student>>> plan, String sheet_name) throws IOException {
		// try (FileOutputStream out = new FileOutputStream(new File(input_excel_file));
		try (FileOutputStream out = new FileOutputStream(
				new File(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "plan.xlsx"));
				XSSFWorkbook workbook = new XSSFWorkbook()) {

			XSSFSheet spreadsheet = workbook.createSheet(sheet_name);
			ArrayList<ArrayList<String>> data = new ArrayList<>();

			plan.forEach(row -> {
				if (row != null) {
					ArrayList<String> dataRow = new ArrayList<>();
					row.forEach(pair -> {
						if (pair != null) {
							dataRow.add(pair.getFirst().getName());
							dataRow.add(pair.getSecond().getName() + pair.getSecond().getSurname());
						}
					});
					data.add(dataRow);
				}
			});

			XSSFRow row;
			int rowid = 0;
			for (ArrayList<String> objectArr : data) {
				row = spreadsheet.createRow(rowid++);
				int cellid = 0;
				for (String obj : objectArr) {
					Cell cell = row.createCell(cellid++);
					cell.setCellValue(obj);
				}
			}
			workbook.write(out);

		} catch (FileNotFoundException e) {
			System.err.println("cannot open output excel file ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
