package ikom.ikom_einzelgespraeche;

import java.io.File;
import java.util.Date;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

public class Reader {
	List<Student> student_list;
	List<Company> company_list;
	String input_excel_file;
	int[] indexColor = { 24, 26, 29, 41, 47, 49, 42, 44, 46, 48, 50, 10, 11, 12, 13, 14, 15, 22 };
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd.MM.yyyy");
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	public Reader(String input_excel_file) throws IOException {
		this.student_list = new ArrayList<>();
		this.company_list = new ArrayList<>();
		this.input_excel_file = input_excel_file;
		read_data_from_excel();
	}

	void read_data_from_excel() throws IOException {
		try (FileInputStream file = new FileInputStream(new File(input_excel_file));
				Workbook workbook = new XSSFWorkbook(file)) {

			// Student
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				student_list.add(Student.convert_row_to_student(row));
			}

			// Company
			sheet = workbook.getSheetAt(1);
			for (Row row : sheet) {
				company_list.add(Company.convert_row_to_company(row));
			}

			sheet = workbook.getSheetAt(2);
			List<List<String>> matches = new ArrayList<>();
			for (Row row : sheet) {
				matches.add(convert_row_to_match(row));
			}

			for (List<String> match : matches) {
				int comp_index = company_list.indexOf(new Company(match.get(0)));
				if (comp_index < 0)
					System.err.println("cannot find the company: " + match.get(0));
				int stu_index = student_list.indexOf(new Student(match.get(1), match.get(2)));
				if (stu_index < 0)
					System.err.println("cannot find the student: " + match.get(1) + " " + match.get(2));
				company_list.get(comp_index).matched_students.add(student_list.get(stu_index));
			}

			for (Company comp : company_list)
				comp.setMatched_students(comp.getMatched_students().stream().distinct().collect(Collectors.toList()));

		} catch (FileNotFoundException e) {
			System.err.println("cannot open input excel file " + input_excel_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void write_plan_to_excel(List<List<Pair<Company, Student>>> plan, List<LocalTime> timeSlot_list,
			String output_excel_file, String sheet_name) throws IOException {
		System.out.println("WRITING PLAN TO FILE with sheet name: " + sheet_name);

		Date date = null;
		try {
			date = this.simpleDateFormat.parse(sheet_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		try (FileOutputStream out = new FileOutputStream(new File(output_excel_file));
				XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet spreadsheet = workbook.createSheet(sheet_name);
			int startRowOfTable = 0;

			ArrayList<ArrayList<String>> data = new ArrayList<>();
			// Title
			ArrayList<String> heading = new ArrayList<>();
			heading.add("Plan for " + sheet_name);
			data.add(heading);
			startRowOfTable++;

			for (int timeSlot = 0; timeSlot < plan.size(); timeSlot++) {
				List<Pair<Company, Student>> row = plan.get(timeSlot);
				if (row != null) {
					ArrayList<String> dataRow = new ArrayList<>();
					LocalTime localTime = timeSlot_list.get(timeSlot);
					LocalDateTime localDateTime = LocalDateTime.of(localDate, timeSlot_list.get(timeSlot));
					dataRow.add(localTime.toString() + " - " + localTime.plusMinutes(45).toString());

					row.forEach(pair -> {
						if (pair != null) {
							dataRow.add(pair.getFirst().getName());
							dataRow.add(pair.getSecond().getName() + " " + pair.getSecond().getSurname());

							// add info for the word letters
							pair.getValue().getMatched_company().put(pair.getKey(), localDateTime);
							pair.getKey().getMatched_plan().put(pair.getValue(), localTime);
						}
					});
					data.add(dataRow);
				}
			}

			XSSFRow row;
			int rowid = 0;
			List<String> colorMap = new ArrayList<>();
			XSSFCellStyle tableStyle = workbook.createCellStyle();

			for (ArrayList<String> objectArr : data) {

				if (rowid < startRowOfTable) {
					row = spreadsheet.createRow(rowid++);
					Cell cell = row.createCell(0);
					cell.setCellValue(objectArr.get(0));
				} else {
					row = spreadsheet.createRow(rowid++);
					int cellid = 0;
					for (String obj : objectArr) {

						if (cellid == 0) {
							Cell cell = row.createCell(cellid++);
							cell.setCellValue(obj);
						} else {
							if (cellid % 2 == 1) {
								if (!colorMap.contains(obj)) {
									colorMap.add(obj);
								}
								tableStyle = workbook.createCellStyle();
								tableStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
								tableStyle.setFillForegroundColor(
										IndexedColors.fromInt(indexColor[colorMap.indexOf(obj)]).getIndex());
								tableStyle.setBorderTop(BorderStyle.MEDIUM);
								tableStyle.setBorderBottom(BorderStyle.MEDIUM);
								tableStyle.setBorderLeft(BorderStyle.MEDIUM);
								tableStyle.setBorderRight(BorderStyle.MEDIUM);
							}
							Cell cell = row.createCell(cellid++);
							cell.setCellValue(obj);
							cell.setCellStyle(tableStyle);
						}
					}
				}
			}

			for (int i = 0; i < data.get(startRowOfTable).size(); i++) {
				spreadsheet.autoSizeColumn(i);
			}

			// style title
			XSSFCellStyle titleStyle = workbook.createCellStyle();
			Font titleFont = workbook.createFont();
			titleFont.setBold(true);
			titleFont.setFontHeightInPoints((short) 20);
			titleStyle.setFont(titleFont);
			spreadsheet.getRow(0).getCell(0).setCellStyle(titleStyle);
			workbook.write(out);

		} catch (FileNotFoundException e) {
			System.err.println("cannot open output excel file ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void create_student_letters(List<List<Pair<Company, Student>>> plan, List<LocalTime> timeSlot_list,
			String output_folder, String date, String input_word_template) throws IOException {

		for (Student student : this.student_list) {
			HashMap<String, String> data_to_change = new HashMap<>();
			String studentName = student.getName() + student.getSurname();
			data_to_change.put("StudentName", studentName);
			data_to_change.put("MesseName", "IKOM 2023"); //////////////////////////////////////
			String cancelDeadline = "Dienstag, den 25. Januar um 11:00 Uhr"; //////////////////////////////////////
			data_to_change.put("AbsageFrist", cancelDeadline);
			String contactPerson = "Bach Ngoc Doan";
			data_to_change.put("AnsprechpartnerInfo", contactPerson);

			try (XWPFDocument templateDoc = new XWPFDocument(new FileInputStream(input_word_template));
					FileOutputStream out = new FileOutputStream(
							output_folder + File.separator + studentName + ".docx");) {

				// Replace placeholders with corresponding values from the Excel row
				for (XWPFParagraph paragraph : templateDoc.getParagraphs()) {
					List<XWPFRun> runs = paragraph.getRuns();
					if (runs != null) {
						for (XWPFRun run : runs) {
							String text = run.getText(0);
							if (text != null) {
								for (String key : data_to_change.keySet()) {
									if (text.contains(key)) {
										text = text.replace(key, data_to_change.get(key));
										run.setText(text, 0);
									}
								}
							}
						}
					}
				}

				XWPFTable table = templateDoc.getTables().get(0);
				// System.out.println(table.getRow(0).getCell(0).getText());
				if (table == null) {
					System.out.println("Table not found in the document.");
					return;
				}

				// Check if the table has enough rows
				int numRowsNeeded = student.getMatched_company().size();
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

				int rowCount = 1;
				Map<Company, LocalDateTime> sortedMap = student.getMatched_company().entrySet().stream()
						.sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey,
								Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

				for (Entry<Company, LocalDateTime> entry : sortedMap.entrySet()) {
					XWPFTableRow row = rows.get(rowCount);
					List<XWPFTableCell> cells = row.getTableCells();
					cells.get(0).setText(entry.getValue().toLocalDate().format(this.dateFormatter).toString());
					cells.get(1).setText(entry.getValue().toLocalTime().format(this.timeFormatter).toString());
					cells.get(2).setText(entry.getKey().getName());
					rowCount++;
				}

				templateDoc.write(out);

			}

		}
		System.out.println("DONE WRITING STUDENT LETTERS");

	}

	static List<String> convert_row_to_match(Row row) {
		return Arrays.asList(row.getCell(2).getRichStringCellValue().getString().trim(),
				row.getCell(0).getRichStringCellValue().getString(),
				row.getCell(1).getRichStringCellValue().getString());
	}

	static void pretty_print_plan(List<List<Pair<Company, Student>>> plan) { // Loop through all rows
		System.out.println("PRINTING THE PLAN");

		for (int timeSlot = 0; timeSlot < plan.size(); timeSlot++) {
			List<Pair<Company, Student>> row = plan.get(timeSlot);
			if (row != null) {
				System.out.print("timeSlot " + (timeSlot + 1) + " : ");
				row.forEach(pair -> {
					if (pair != null)
						if (pair.getSecond() != null)
							System.out.print("[" + pair.getFirst().getName() + " , " + pair.getSecond().getName() + " "
									+ pair.getSecond().getSurname() + "]     ");
						else
							System.out.print("[" + pair.getFirst().getName() + " , NOT MACHTED]     ");
				});
				System.out.println();
			}
		}
		System.out.println();
	}

	static void pretty_print_company_distribution(List<List<Company>> plan) {
		System.out.println("PRINTING COMPANY DISTRIBUTION");
		for (int timeSlot = 0; timeSlot < plan.size(); timeSlot++) {
			List<Company> column = plan.get(timeSlot);
			System.out.print("timeSlot " + (timeSlot + 1) + " : [");
			column.forEach(comp -> {
				System.out.print(comp.getName() + " ---- ");
			});
			System.out.println("]");
		}
	}

	// this method is not used
	static <T> List<T> read_one_sheet(String input_excel_file, int sheet_index, Function<Row, T> convert_func)
			throws IOException {

		List<T> return_list = new ArrayList<>();

		try (FileInputStream file = new FileInputStream(new File(input_excel_file));
				Workbook workbook = new XSSFWorkbook(file)) {

			Sheet sheet = workbook.getSheetAt(sheet_index);

			for (Row row : sheet) {
				return_list.add(convert_func.apply(row));
			}

		} catch (FileNotFoundException e) {
			System.err.println("cannot open input excel file " + input_excel_file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return return_list;
	}

}
