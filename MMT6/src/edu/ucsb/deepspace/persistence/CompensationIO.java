package edu.ucsb.deepspace.persistence;

import java.io.IOException;
import java.util.Calendar;

import edu.ucsb.deepspace.business.Bookkeeper;

public class CompensationIO {
	
	public static void appendToFile(String data, Calendar time, String reflName) {
		
		//should be something that creates file if it doesn't exist and put this at the top:
		//"Refl Name","Month","Day","Year","Hour","Minute","Second","Radius","Theta","Phi","Mode"
		
		int month = time.get(Calendar.MONTH) + 1;
		String timestamp = month + "," + time.get(Calendar.DAY_OF_MONTH) + "," + time.get(Calendar.YEAR) + "," + 
				time.get(Calendar.HOUR_OF_DAY) + "," + time.get(Calendar.MINUTE) + "," + time.get(Calendar.SECOND);
		
		String mode = Bookkeeper.getInstance().getTrkMeasmode().toString();
		String totalOut = "\"" + reflName + "\"," + timestamp + "," + data + "," + mode;
		
		try {
			Writer.append("compensation.csv", totalOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}