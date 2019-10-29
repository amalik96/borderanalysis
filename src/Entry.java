package com.borderdata.csv;

import java.time.LocalDate;
import java.util.Comparator;

public class Entry implements Comparable<Entry> {
		// define class private variables
		private LocalDate date;
		private String measure;
		private int quantity;
		private String border;
		
		// define what happens when new class is created
		public Entry(LocalDate d, String m, int q, String b) {
			this.date = d;
			this.measure = m;
			this.quantity = q;
			this.border = b;
		}
		
		// define getters and setters for each private variable
		
		public LocalDate getDate() {
			return date;	
		}
		public void setDate(LocalDate d) {
			this.date = d;	
		}
		
		public String getMeasure() {
			return measure;
		}
		public void setMeasure(String m) {
			this.measure = m;
		}
		
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int q) {
			this.quantity = q;
		}
		
		public String getBorder() {
			return border;
		}
		public void setBorder(String b) {
			this.border = b;
		}
		
		// define converter to String
		public String toString() {
			return System.lineSeparator() + "[" + date.toString() + "," + measure + "," + quantity + "," + border + "]";
			
		}

		// define comparators which will help us sort
		
		public static Comparator<Entry> reverseDateComparator = new Comparator<Entry>() { 
			@Override  
			public int compare(Entry e1, Entry e2) {
				LocalDate l1 = e1.getDate();
				LocalDate l2 = e2.getDate();

				return (-1) * (l1.compareTo(l2));
			}
		};

		public static Comparator<Entry> reverseQuantityComparator = new Comparator<Entry>() { 
			@Override  
			public int compare(Entry e1, Entry e2) {
				return (-1) * (e1.getQuantity() - e2.getQuantity());
			}
		};
		
		public static Comparator<Entry> reverseMeasureComparator = new Comparator<Entry>() { 
			@Override  
			public int compare(Entry e1, Entry e2) {
				return (-1) * e1.getMeasure().compareTo(e2.getMeasure());
			}
		};
		
		public static Comparator<Entry> reverseBorderComparator = new Comparator<Entry>() { 
			@Override  
			public int compare(Entry e1, Entry e2) {
				return (-1) * e1.getBorder().compareTo(e2.getBorder());
			}
		};
		
		// not using but have to define
		@Override
		public int compareTo(Entry o) {
			return 0;
		}
}
