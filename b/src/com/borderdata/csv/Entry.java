package com.borderdata.csv;

import java.time.LocalDate;
import java.util.Comparator;

public class Entry implements Comparable<Entry> {
		
		private LocalDate date;
		private String measure;
		private int quantity;
		
		public Entry(LocalDate d, String m, int q) {
			this.date = d;
			this.measure = m;
			this.quantity = q;
		}
		
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
		
		public String toString() {
			return System.lineSeparator() + "[" + date.toString() + "," + measure + "," + quantity + "]";
			
		}

		public static Comparator<Entry> dateComparator = new Comparator<Entry>() { 
			@Override  
			public int compare(Entry e1, Entry e2) {

				LocalDate l1;
				LocalDate l2;
				l1 = e1.getDate();
				l2 = e2.getDate();

				int compareValue;
				compareValue = l1.compareTo(l2);
				
				if(compareValue == -1) {
					return 1;
				}
				else if (compareValue == 1) {
					return -1;
				}
				else {
					return 0;
				}


			}
		};
}