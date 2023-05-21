package ikom.ikom_einzelgespraeche;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;

public class Main {

	public static void main(String[] args) {

		// set input file
		String[] input_path = { "src", "main", "java", "ikom", "input" };
		String input_folder = String.join(File.separator, input_path) + File.separator;

		// set output file
		String[] output_path = { "src", "main", "java", "ikom", "output" };
		String output_folder = String.join(File.separator, output_path) + File.separator;

		// TODO: check for scan error
		Scanner sc = new Scanner(System.in, "UTF-8"); // System.in is a standard input stream

		Reader reader = null;
		try {
			reader = new Reader(input_folder + "input.xlsx");
		} catch (IOException e) {
			e.printStackTrace();
			sc.close();
			return;
		}

		List<Date> date_list = new ArrayList<>();
		for (Company comp : reader.company_list)
			date_list.add(comp.getFair_day());
		date_list = date_list.stream().distinct().collect(Collectors.toList());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd.MM.yyyy");
		System.out.println("list of all dates:");
		for (int i = 0; i < date_list.size(); i++) {
			// System.out.println(i + 1 + ". " + date_list.get(i));
			System.out.println(i + 1 + ". " + simpleDateFormat.format(date_list.get(i)));
		}

		while (true) {
			System.out.println("\nEnter a valid date or \"finished\" to end: ");
			String input = sc.nextLine();

			if (input.equals("finished"))
				break;
			else {
				try {
					int date_index = Integer.parseInt(input.substring(0, 1)) - 1;

					System.out.print("Enter column length: ");
					int COLUMN_LENGTH = sc.nextInt();
					sc.nextLine(); // consume the line seperator

					List<LocalTime> timeSlot_list = new ArrayList<>();
					System.out.println("Enter " + COLUMN_LENGTH + " time slots. For example: \"09:30 *ENTER*\"");

					for (int i = 0; i < COLUMN_LENGTH; i++) {
						boolean valid = false;
						while (!valid)
							try {
								String input_ts = sc.next();
								LocalTime ts = LocalTime.parse(input_ts, DateTimeFormatter.ofPattern("HH:mm"));
								timeSlot_list.add(ts);
								valid = true;
								System.out.println("current time slots are: " + timeSlot_list);
							} catch (Exception e) {
								System.out.println("Invalid time slot. Please enter a new one.");
							}
					}
					sc.nextLine(); // consume the line seperator

					CompanyDistributor distributor = new CompanyDistributor(date_list.get(date_index),
							reader.company_list, COLUMN_LENGTH);

					boolean good_distribution = false;
					do {
						distributor.permutate_distribution();
						System.out.println(
								"\nIs this distribution good? Enter \"ok\" or something else to create new distribution: ");
						if (sc.nextLine().equals("ok"))
							good_distribution = true;
					} while (!good_distribution);

					List<List<Pair<Company, Student>>> empty_plan = distributor.create_empty_plan();
					Matching match = new Matching(COLUMN_LENGTH, empty_plan.get(0).size(), empty_plan, sc);
					match.create_plan();

					if (!match.isPlan_created_is_ok()) {
						System.out.println("UNABLE TO CREATE A NEW PLAN FOR THIS DISTRIBUTION");
					} else {
						reader.write_plan_to_excel(match.plan, timeSlot_list, output_folder + "plan.xlsx",
								simpleDateFormat.format(date_list.get(date_index)));
						// the resulted plan is: "match.plan"
						reader.create_student_letters(match.plan, timeSlot_list,
								output_folder + "studentLetters" + File.separator,
								simpleDateFormat.format(date_list.get(date_index)));

					}

				} catch (Exception e) {
					System.out.println(
							"UNABLE TO CREATE A PLAN. PLEASE CHECK IF THE GIVEN PARAMETERS ARE COMPATIBLE WITH THE FIXED CONSTRAINTS OF THE PROGRAM!");
					continue;
				}

			}
		}
		sc.close();
	}

}
