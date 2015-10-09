package com.summary.reference.clean;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.boundary.sentence.TextContent;

public class ReferenceSummaryCleaning {
	private static Logger logger = Logger.getLogger("MyLog");

	public static void main(String[] args) {
		File folder = new File(args[0]);
    	if(!folder.isDirectory()){
    		System.out.println("Input must be a directory.");
    		System.exit(1);
    	}
		File[] listOfFiles = folder.listFiles();
		for(int i = 0; i < listOfFiles.length; i ++){
			String fileContent = "";
			try {
				fileContent = FileUtils.readFileToString(new File (listOfFiles[i].getAbsolutePath()));
			} catch (IOException e) {
				logger.info("Error in reading the text file\n");
			}
			
			TextContent t = new TextContent(); //creating TextContent object
			t.setText(fileContent); //setting the text
			t.setSentenceBoundary(); //detecting the sentence boundaries
			String[] array = t.getSentence();//getting the sentences in an array
			
			String referenceSummaryToBeWritten = "";
			for (String str : array){
				referenceSummaryToBeWritten += str +"\n";
			}
			
			File referenceSummaryFile = new File(FilenameUtils.getFullPath(listOfFiles[i].toString()) + listOfFiles[i].getName() +  "-automatic.txt");
			try {
				FileUtils.write(referenceSummaryFile, referenceSummaryToBeWritten);
			} catch (IOException e) {
				logger.info("Error in writing summary");
			}
		}

	}
	
	public static void initiateLogger(File file){
		FileHandler fileHandler;
		try {
			// This block configure the logger with handler and formatter
			fileHandler = new FileHandler(file.getParentFile().getAbsolutePath()+ "/"
					+ "log.txt", true);
			logger.addHandler(fileHandler);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
