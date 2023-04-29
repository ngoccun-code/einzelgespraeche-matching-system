package ikom.ikom_einzelgespraeche;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

public class Company {
	String name;
	Date fair_day;
	List<Student> matched_students = new ArrayList<>();

	public Company(String name, Date fair_day) {
		this.name = name;
		this.fair_day = fair_day;
	}

	public Company(String name) {
		this.name = name;
		this.fair_day = null;
	}

	static Company convert_row_to_company(Row row) {
		return new Company(row.getCell(0).getRichStringCellValue().getString().trim(),
				row.getCell(1).getDateCellValue());
	}

	@Override
	public String toString() {
		return "*Company* fair_day: " + new SimpleDateFormat("dd.MM.yyyy").format(fair_day) + ", name: " + name;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Company)) {
			return false;
		}
		Company c = (Company) o;
		return name.trim().equals(c.name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getFair_day() {
		return fair_day;
	}

	public void setFair_day(Date fair_day) {
		this.fair_day = fair_day;
	}

	public List<Student> getMatched_students() {
		return matched_students;
	}

	public void setMatched_students(List<Student> matched_students) {
		this.matched_students = matched_students;
	}

}
