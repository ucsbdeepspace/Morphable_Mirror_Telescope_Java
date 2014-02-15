package edu.ucsb.deepspace.persistence;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Writer {
	
	public static void write(String filename, String data) throws IOException {
		FileUtils.writeStringToFile(new File(filename), data);
	}
	
	public static void append(String filename, String data) throws IOException {
		FileUtils.writeStringToFile(new File(filename), data+"\n", true);
	}

}