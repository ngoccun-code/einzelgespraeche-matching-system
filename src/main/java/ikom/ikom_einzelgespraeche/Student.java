package ikom.ikom_einzelgespraeche;

import org.apache.poi.ss.usermodel.Row;

class Student {
	String name;
	String surname;
	String email;
	int phone_number;

	static Student convert_row_to_student(Row row) {
		return new Student(row.getCell(0).getRichStringCellValue().getString().trim(),
				row.getCell(1).getRichStringCellValue().getString().trim(),
				row.getCell(2).getRichStringCellValue().getString().trim(), (int) row.getCell(3).getNumericCellValue());
	}

	@Override
	public String toString() {
		return "*Student* name: " + name + ", surname: " + surname + ", email: " + email + ", phone number: "
				+ phone_number;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Student)) {
			return false;
		}
		Student c = (Student) o;
		return name.toLowerCase().trim().equals(c.name.toLowerCase())
				&& surname.toLowerCase().trim().equals(c.surname.toLowerCase());
	}

	public Student(String name, String surname, String email, int phone_number) {
		this.name = name;
		this.surname = surname;
		this.phone_number = phone_number;
		this.email = email;
	}

	public Student(String name, String surname) {
		this.name = name;
		this.surname = surname;
		this.phone_number = 0;
		this.email = "";
	}

	public Student(String name, String surname, int phone_number) {
		this.name = name;
		this.surname = surname;
		this.phone_number = phone_number;
		this.email = "";
	}

	public Student(String name, String surname, String email) {
		this.name = name;
		this.surname = surname;
		this.phone_number = 0;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(int phone_number) {
		this.phone_number = phone_number;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
