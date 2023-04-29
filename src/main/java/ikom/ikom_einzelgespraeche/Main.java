package ikom.ikom_einzelgespraeche;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;

public class Main {

	public static void main(String[] args) {

		// TODO: check for scan error
		Scanner sc = new Scanner(System.in, "UTF-8"); // System.in is a standard input stream
		System.out.print("Enter column length: ");
		int COLUMN_LENGTH = sc.nextInt();
		sc.nextLine(); // consume the line seperator

		String input_excel_file = System.getProperty("user.home") + File.separator + "Desktop" + File.separator
				+ "Einzelgesprächsteilnehmer_Bau23.xlsx";

		Reader reader = null;
		try {
			reader = new Reader(input_excel_file);
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
						reader.write_plan_to_excel(match.plan, simpleDateFormat.format(date_list.get(date_index))); // date_list.get(date_index)
					}

				} catch (Exception e) {
					continue;
				}

			}
		}
		sc.close();
	}

}
