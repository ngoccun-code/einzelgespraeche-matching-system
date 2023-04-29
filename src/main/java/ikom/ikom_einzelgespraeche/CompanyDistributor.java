package ikom.ikom_einzelgespraeche;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;

public class CompanyDistributor {

	Date date;
	List<Company> companies;
	int column_length;
	List<List<Company>> distribution;

	public CompanyDistributor(Date date, List<Company> all_companies, int column_length) {
		this.date = date;
		this.column_length = column_length;
		this.companies = all_companies.stream().filter(comp -> comp.fair_day.equals(date)).collect(Collectors.toList());
		this.distribution = distribute_companies();
	}

	private List<List<Company>> distribute_companies() {
		System.out.println("			DISTRIBUTE COMPANIES OF " + date + "\n");
		this.companies.sort((c1, c2) -> c2.matched_students.size() - c1.matched_students.size());
		List<Integer> avaiable_entries = new ArrayList<>();
		List<List<Company>> distribution = new ArrayList<>();
		for (Company c : this.companies) {
			boolean found = false;
			for (int i = 0; i < avaiable_entries.size(); i++) {
				if (c.getMatched_students().size() <= avaiable_entries.get(i)) {
					found = true;
					distribution.get(i).add(c);
					avaiable_entries.set(i, avaiable_entries.get(i) - c.getMatched_students().size());
				}
			}
			if (!found) {
				distribution.add(new ArrayList<Company>());
				distribution.get(distribution.size() - 1).add(c);
				avaiable_entries.add(column_length - c.getMatched_students().size());
			}
		}
		return distribution;
	}

	void permutate_distribution() {
		distribution.forEach(column -> {
			Collections.shuffle(column);
		});
		Reader.pretty_print_company_distribution(distribution);
	}

	List<List<Pair<Company, Student>>> create_empty_plan() {
		List<List<Pair<Company, Student>>> plan = new ArrayList<>();
		for (int i = 0; i < column_length; i++) {
			plan.add(new ArrayList<>());
		}

		distribution.forEach(companies_for_current_column -> {
			int row = 0;
			for (Company comp : companies_for_current_column) {
				int amount_of_talks = comp.getMatched_students().size();
				while (amount_of_talks > 0) {
					plan.get(row).add(new Pair<>(comp, null));
					row++;
					amount_of_talks--;
				}
			}
			while (row < column_length)
				plan.get(row++).add(null);
		});

		return plan;
	}
}
