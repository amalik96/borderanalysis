package com.borderdata.csv;

import java.io.*;
import java.time.*;
import java.time.format.*; 
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math.*;

public class csvreader {

	/// sort in descending order by Date, Value (Quantity), Measure, and Border
	public static void sortEntries(ArrayList<Entry> entries) {

		// sort by least important field first to get result

		Collections.sort(entries, Entry.reverseBorderComparator);

		Collections.sort(entries, Entry.reverseMeasureComparator);

		Collections.sort(entries, Entry.reverseQuantityComparator);

		Collections.sort(entries, Entry.reverseDateComparator);
	}

	public static void printStats(PrintWriter pw, String currentBorder, LocalDate date, String currentMeasure, int currentSum, long average) { 
		String formattedDate = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		pw.print(currentBorder + ",");
		pw.print(formattedDate + " 12:00:00 AM,");
		pw.println(currentMeasure + "," + currentSum + "," + average);
	
	}

	public static int monthlyAverageCrossingsInPreviousMonths(String border, String measure, int m, ArrayList<Entry> entries) {
		// for all months prior to currentMonth, sum the number of crossings over that border
		//System.out.println("...monthlyAverageCrossingsInPreviousMonths...");
		//System.out.println("border:" + b + "month:" + m);
		//System.out.println("entries:" + entries);

		// loop until currentMonth is different
		int j = 0;
		while (entries.get(j).getDate().getMonthValue() == m) {
			j++;
		}

		// start monthly average accumulation from previous month onwards
		int currentMonth = entries.get(j).getDate().getMonthValue();
		//System.out.println("currentMonth:" + currentMonth);

		int sum = 0;
		int count = 0;
		int[] monthlySums = {0,0,0,0,0,0,0,0,0,0,0,0};

		// loop through all entries
		for(int i=j; i<entries.size(); i++) {
			Entry e = entries.get(i);
			String thisBorder = e.getBorder();
			int thisMonth = e.getDate().getMonthValue();
			String thisMeasure = e.getMeasure();
			//System.out.println("i:" + i + " thisBorder:" + thisBorder + " thisMonth:" + thisMonth);

			// if not new month, only add crossing count if it is the right border
			if (thisMonth != currentMonth) {
				//System.out.println("new month encountered...");
				// this is a new month, so save the monthly average
				if (count > 0) {
					monthlySums[currentMonth-1] = sum;
					//System.out.println("monthlySums[" + (currentMonth-1) + "] being saved is " + monthlySums[currentMonth-1]);
				}
				//System.out.println("resetting currentMonth, sum, and count");
				currentMonth = thisMonth;
				sum = 0;
				count = 0;
			}

			if (thisBorder.equals(border) && thisMeasure.equals(measure)) {
				sum = sum + e.getQuantity();
				count++;
				//System.out.println("border " + b + " month " + currentMonth + " adding " + e.getQuantity() + " sum " + sum + " count " + count);
			}

			if (i+1 == entries.size()) {
				// last entry encountered
				if (count > 0) {
					monthlySums[currentMonth-1] = sum;
					//System.out.println("monthlySums[" + (currentMonth-1) + "] being saved is " + monthlySums[currentMonth-1]);
				}
			}
		}

		//System.out.println("computing average of monthly sums");
		int totalSum = 0;
		// average the monthly sums that we saved
		for (int i=0; i<(m-1); i++) {
			totalSum = totalSum + monthlySums[i];
			//System.out.println("monthlySums[i]:" + monthlySums[i] + " totalSum:" + totalSum);
		}

		//System.out.println("dividing totalSum by:" + (m-1));
		double roundedAverage = ((double)totalSum / (double)(m - 1));
		//System.out.println("average: " + roundedAverage);

		// round to the closest integer up or down
		roundedAverage = Math.round(roundedAverage);
		//System.out.println("rounded average: " + roundedAverage);

		return (int) roundedAverage;
	}

	public static void main(String[] args) {
		String inputFile = "Border_Crossing_Entry_Data.csv";
		File file = null;
		BufferedReader br = null;
		String outputDir = "output";
		String outputFile = "output/report.csv";
		PrintWriter pw = null;
		String line = "";
		String csvSplitBy = ",";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/uuuu hh:mm:ss a");
		ArrayList<Entry> entries = new ArrayList<Entry>();

		try {
			// prepare to read file
			br = new BufferedReader(new FileReader(inputFile));

			// prepare to write file
			file = new File(outputDir);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(outputFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			pw = new PrintWriter (outputFile);

			// get rid of useless header line
			br.readLine();

			while ((line = br.readLine()) != null) {
				// read each line and put comma-separated parts into array crossing
				String[] crossing = line.split(csvSplitBy);

				//Fill the array with this line's data
				LocalDate tempDate 			= LocalDate.parse(crossing[4], formatter);
				String tempMeasure			= crossing[5];
				int tempQuantity   			= Integer.parseInt(crossing[6]);
				String tempBorder			= crossing[3];

				// create an Entry object with useful parts and add to array list entries
				entries.add(new Entry(tempDate, tempMeasure, tempQuantity, tempBorder));
			}

			// array of entries has been filled with file data, now start processing the array

			// sort array list entries first by measure then by month
			sortEntries(entries);

			// get the month and measure from date of the first entry
			int currentYear = entries.get(0).getDate().getYear();
			int currentMonth = entries.get(0).getDate().getMonthValue();
			int currentDay = entries.get(0).getDate().getDayOfMonth();
			String currentMeasure = entries.get(0).getMeasure();
			String currentBorder = entries.get(0).getBorder();

			// initialize counters
			int currentSum = 0;
			int count = 0;

			// print output header
			pw.println("Border,Date,Measure,Value,Average");

			// process sorted entries
			for(int i=0; i<entries.size(); i++) {
				Entry e = entries.get(i);
				int thisYear = e.getDate().getYear();
				int thisMonth = e.getDate().getMonthValue();
				int thisDay = e.getDate().getDayOfMonth();
				String thisMeasure = e.getMeasure();
				String thisBorder = e.getBorder();

				// if measure changes
				if (! thisMeasure.equals(currentMeasure)) {
					//System.println("measure changed from currentMeasure " + currentMeasure + " to thisMeasure " + thisMeasure);
					// if new measure is encountered, print month measure stats
					int m = monthlyAverageCrossingsInPreviousMonths(currentBorder, currentMeasure, currentMonth, entries);
					printStats(pw, currentBorder, LocalDate.of(currentYear,currentMonth,currentDay), currentMeasure, currentSum, m);
					// reset variables for new month measure combination
					currentSum = 0;
					count = 0;
					currentMeasure = thisMeasure;
					if (thisMonth != currentMonth) {
						currentMonth = thisMonth;
					}
					if (thisYear != currentYear) {
						currentYear = thisYear;
					}
					if (! thisBorder.equals(currentBorder)) {
						currentBorder = thisBorder;
					}
				}

				// always add the current entry's quantity to the running total and update count
				currentSum = currentSum + e.getQuantity();
				count++;

				// if you are on the last entry, print stats for the last month measure combination
				if(i+1 == entries.size()) {
					//System.out.println("On last line...");
					int m = monthlyAverageCrossingsInPreviousMonths(currentBorder, currentMeasure, currentMonth, entries);
					printStats(pw, currentBorder, LocalDate.of(currentYear,currentMonth,currentDay), currentMeasure, currentSum, m);
				}
			}

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
			if (pw != null) {
				pw.close();
			}
		}
	}
}
