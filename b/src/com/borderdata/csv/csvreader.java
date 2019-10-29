package com.borderdata.csv;

import com.borderdata.csv.Entry;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.time.*;
import java.time.format.*; 

import java.util.ArrayList;
import java.util.Collections;



public class csvreader {

	public static void main(String[] args) {

		String csvFile = "borderdata.csv";
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";

		int current=0;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/uuuu hh:mm:ss a");

		ArrayList<Entry> entries = new ArrayList<Entry>();

		try {

			br = new BufferedReader(new FileReader(csvFile));

			// get rid of useless header line
			br.readLine();
			
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] crossing = line.split(csvSplitBy);

				//Fill the array with this line's data
				LocalDate tempDate 			= LocalDate.parse(crossing[4], formatter);
				String tempMeasure			= crossing[5];
				int tempQuantity   			= Integer.parseInt(crossing[6]);

				entries.add(new Entry(tempDate, tempMeasure, tempQuantity));
				current++;
			}

			//array of entries has been filled with file data, now start processing the array
			System.out.println("Before sorting: " + entries);
			Collections.sort(entries, Entry.dateComparator);
			System.out.println("After sorting: " + entries);

			//getting the month from date
			int currentMonth = entries.get(0).getDate().getMonthValue();
			int currentsum=0;
			int count = 0;
			int i;


			for(i=0; i<entries.size(); i++) {

				if(entries.get(i).getDate().getMonthValue() != currentMonth) {
					//output prev month sum and avg
					System.out.println("Month Number:" + currentMonth + 
							" Sum:" + currentsum + 
							" Average:" + currentsum/(count));
					// if new month encountered, reset sum, count, and current month
					currentsum = 0;
					count = 0;
					currentMonth = entries.get(i).getDate().getMonthValue();
				}

				// always add the current entry's quantity to the running total and update count
				currentsum = currentsum + entries.get(i).getQuantity();
				count = count + 1;

				// if you are on the last entry, output sum and avg
				if(i+1 == entries.size()) {
					//output prev month sum and avg
					System.out.println("Month Number:" + currentMonth + 
							" Sum:" + currentsum + 
							" Average:" + currentsum/(count));
				}
			} // for

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
