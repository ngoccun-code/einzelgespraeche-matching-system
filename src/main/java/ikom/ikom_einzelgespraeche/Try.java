package ikom.ikom_einzelgespraeche;

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Try {

	// any exceptions need to be caught
	public static void main(String[] args) throws Exception {
		String[] input_path = { "src", "main", "java", "ikom", "input" };
		String input_folder = String.join(File.separator, input_path) + File.separator;
		String[] output_path = { "src", "main", "java", "ikom", "output" };
		String output_folder = String.join(File.separator, output_path) + File.separator + "studentLetters"
				+ File.separator;

		// System.out.println("WRITING STUDENT LETTERS");

		// for (int i = 0; i < data.size(); i++) {

		HashMap<String, String> rowData = new HashMap<>();
		String studentName = "Anna";
		rowData.put("StudentName", studentName);
		rowData.put("MesseName", "IKOM 2023");
		String cancelDeadline = "Dienstag, den 25. Januar um 11:00 Uhr";
		rowData.put("AbsageFrist", cancelDeadline);
		String contactPerson = "Bach Ngoc Doan";
		rowData.put("AnsprechpartnerInfo", contactPerson);

		try (XWPFDocument templateDoc = new XWPFDocument(
				new FileInputStream(input_folder + "Student_Bestätigung.docx"));
				FileOutputStream out = new FileOutputStream(output_folder + File.separator + studentName + ".docx");) {

			// Replace placeholders with corresponding values from the Excel row
			for (XWPFParagraph paragraph : templateDoc.getParagraphs()) {
				List<XWPFRun> runs = paragraph.getRuns();
				if (runs != null) {
					for (XWPFRun run : runs) {
						String text = run.getText(0);
						if (text != null) {
							for (String key : rowData.keySet()) {
								if (text.contains(key)) {
									text = text.replace(key, rowData.get(key));
									run.setText(text, 0);
								}
							}
						}
					}
				}
			}

			// Map<Student, List<Company, LocalTime> >
			List<List<String>> tableData = new ArrayList<>();
			tableData.add(Arrays.asList("Tue", "10:00", "CompanyA"));
			tableData.add(Arrays.asList("Wed", "11:00", "CompanyB"));
			tableData.add(Arrays.asList("Wed", "14:00", "CompanyC"));

			XWPFTable table = templateDoc.getTables().get(0);
			// System.out.println(table.getRow(0).getCell(0).getText());
			if (table == null) {
				System.out.println("Table not found in the document.");
				return;
			}
			// Check if the table has enough rows
			int numRowsNeeded = tableData.size();
			int numRowsPresent = table.getRows().size() - 1; // Subtracting header row
			if (numRowsNeeded > numRowsPresent) {
				int numRowsToAdd = numRowsNeeded - numRowsPresent;
				for (int i = 0; i < numRowsToAdd; i++) {
					XWPFTableRow newRow = table.createRow();
					CTTrPr trPr = newRow.getCtRow().addNewTrPr();
					CTHeight ctHeight = trPr.addNewTrHeight();
					ctHeight.setVal(BigInteger.valueOf(500));
				}
			}
			// Fill the table with data
			List<XWPFTableRow> rows = table.getRows();
			for (int i = 1; i <= numRowsNeeded; i++) {
				XWPFTableRow row = rows.get(i);
				List<String> row_Data = tableData.get(i - 1);
				List<XWPFTableCell> cells = row.getTableCells();
				for (int j = 0; j < cells.size(); j++) {
					cells.get(j).setText(row_Data.get(j));
				}
			}

			templateDoc.write(out);
			System.out.println("DONE WRITING STUDENT LETTERS");
		}
		// }

	}
}
