package ikom.ikom_einzelgespraeche;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.util.Pair;

public class Matching {

	List<List<Pair<Company, Student>>> plan;
	int column_length;
	int row_length;
	boolean plan_created_is_ok = false;
	Scanner sc;

	public Matching(int column_length, int row_length, List<List<Pair<Company, Student>>> empty_plan, Scanner sc) {
		this.column_length = column_length;
		this.row_length = row_length;
		this.plan = empty_plan;
		this.sc = sc;
	}

	public boolean isPlan_created_is_ok() {
		return plan_created_is_ok;
	}

	public void setPlan_created_is_ok(boolean plan_created_is_ok) {
		this.plan_created_is_ok = plan_created_is_ok;
	}

	public Scanner getSc() {
		return sc;
	}

	public void setSc(Scanner sc) {
		this.sc = sc;
	}

	void create_plan() {
		create_recursive(0, 0);
	}

	private void create_recursive(int row, int column) {
		if (plan_created_is_ok)
			return;

		List<Pair<Company, Student>> current_row = plan.get(row);
		if (current_row.get(column) != null) {
			Company current_comp = current_row.get(column).getFirst();
			List<Student> matched_students = current_comp.getMatched_students();

			for (Student stu : matched_students) {
				if (plan_created_is_ok)
					return;
				boolean conflict = false;
				// check row conflict : student conflict
				for (int i = 0; i < column; i++) {
					if (current_row.get(i).getSecond().equals(stu)) {
						conflict = true;
						break;
					}
				}
				// check column conflict : duplicate
				for (int i = 0; i < row; i++) {
					if (plan.get(i).get(column).getFirst().equals(current_comp)
							&& plan.get(i).get(column).getSecond().equals(stu)) {
						conflict = true;
						break;
					}
				}
				if (!conflict) {
					current_row.set(column, new Pair<>(current_comp, stu));

					int next_column = plan.get(row).size() > column + 1 ? column + 1 : 0;
					int next_row = next_column == 0 ? row + 1 : row;
					next_row = next_row < column_length ? next_row : -1;

					boolean valid_cell = false;

					while (!valid_cell) {
						if (next_row != -1 && plan.get(next_row) != null
								&& plan.get(next_row).get(next_column) == null) {
							next_column = plan.get(next_row).size() > next_column + 1 ? next_column + 1 : 0;
							next_row = next_column == 0 ? next_row + 1 : next_row;
							next_row = next_row < column_length ? next_row : -1;
						} else {
							valid_cell = true;
						}
					}

					if (next_row == -1) {
						Reader.pretty_print_plan(plan);
						System.out.println("\nIs this plan good? Enter \"ok\" or something else to create new plan: ");
						if (sc.nextLine().equals("ok")) {
							plan_created_is_ok = true;
						}

					} else {
						create_recursive(next_row, next_column);
					}
				}
			}
		}
	}
}
