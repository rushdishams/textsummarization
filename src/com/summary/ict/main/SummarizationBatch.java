package com.summary.ict.main;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.boundary.sentence.TextContent;

import net.sf.classifier4J.summariser.ISummariser;
import net.sf.classifier4J.summariser.SimpleSummariser;

/**
 * The class to instantiate Simple Summariser object. Simple Summariser can be
 * found in Classify4j project
 * 
 * Change from 2.0.0: Generates CSV file that contains article ID, original text and summary
 * Cleans text with a removeGarbage() routine
 * 
 * @author Rushdi Shams
 * @version 2.1.0 October 26, 2015.
 *
 */
public class SummarizationBatch {
	// --------------------------------------------------------------------------------
	// Instance variables
	// --------------------------------------------------------------------------------
	private static String summaryText;
	private static ISummariser summarizer;
	private static Logger logger = Logger.getLogger("MyLog");
	// --------------------------------------------------------------------------------
	// Methods
	// --------------------------------------------------------------------------------

	public static void initiateLogger(File file) {
		FileHandler fileHandler;
		try {
			// This block configure the logger with handler and formatter
			fileHandler = new FileHandler(file.getParentFile().getAbsolutePath() + "/" + "log.txt", true);
			logger.addHandler(fileHandler);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to summarize a given string
	 * 
	 * @param originalText
	 *            is the text to be summarized
	 * @param summarySize
	 *            is in integer denotes the sentences in the summary
	 * @return the summary of the sentence
	 */
	public static String summarize(String originalText, int summarySize) {
		summarizer = new SimpleSummariser();
		try {
			summaryText = summarizer.summarise(originalText, summarySize);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
		return summaryText;
	}// end method

	/**
	 * Method to display banner on cmd line
	 */
	public static void showBanner() {
		System.out.println("//----------------------------------------------------------------------------//");
		System.out.println("\tText Summarizer v-2.1.0, 26/10/2015");
		System.out.println("\t\t\tAuthor: Rushdi Shams");
		System.out.println("\tUSAGE: java -jar textsummary-2.1.0.jar directorypath/ [floating point number]");
		System.out.println("\tUSAGE: [floating point number]: Percentage of Summary (e.g., 0.3 stands for 30%).");
		System.out.println("//----------------------------------------------------------------------------//");
	}

	public static void main(String[] args) {
		showBanner();

		Instant start = Instant.now();

		File folder = new File(args[0]);
		if (!folder.isDirectory()) {
			System.out.println("Input must be a directory.");
			System.exit(1);
		}
		File[] listOfFiles = folder.listFiles();
		File summaryFile = new File(args[0] + "/" + "summary.csv");
		String outputFileContent = "";

		for (int i = 0; i < listOfFiles.length; i++) {
			System.out.println("----Processing " + listOfFiles[i].getAbsolutePath() + "----\n");

			String fileContent = "";
			try {
				fileContent = FileUtils.readFileToString(new File(listOfFiles[i].getAbsolutePath()));
			} catch (IOException e) {
				logger.info("Error reading articl\n");
			}
			if (fileContent.length() == 0) {
				System.out.println("File contains nothing\n");
				continue;
			}

			fileContent = removeGarbage(fileContent);
			TextContent t = new TextContent(); // creating TextContent object
			t.setText(fileContent);
			t.setSentenceBoundary();
			String[] content = t.getSentence();
			System.out.println("Number of Sentences: " + content.length);
			int summaryLength = Math.round((float) content.length * Float.parseFloat(args[1]));
			System.out.println("Summary Length: " + summaryLength);
			String summary = summarize(fileContent, summaryLength).trim();
			summary = summary.replaceAll("\r\n", " ");
			t.setText(summary); // setting the text
			t.setSentenceBoundary(); // detecting the sentence boundaries
			String[] array = t.getSentence();// getting the sentences in an
												// array

			String summaryToBeWritten = "";
			for (String str : array) {
				summaryToBeWritten += str + " ";
			}
			// System.out.println(summaryToBeWritten);
			String articleID = FilenameUtils.removeExtension(FilenameUtils.getName(listOfFiles[i].toString()));
			// outputFileContent += "\"" + articleID + "\"" + "," + "\"" +
			// fileContent.replaceAll("\r\n", " ").replaceAll("\r",
			// "").replaceAll("\n", "").replaceAll("\"", "").replaceAll("\'",
			// "").replaceAll(",", "") + "\"" + "," +
			// summaryToBeWritten.replaceAll("\r\n", " ").replaceAll("\r",
			// "").replaceAll("\n", "").replaceAll("\"", "").replaceAll("\'",
			// "").replaceAll(",", "") + "\n";
			outputFileContent += "\"" + articleID + "\"" + "," + "\"" + fileContent.replaceAll(",", "") + "\"" + ","
					+ summaryToBeWritten.replaceAll(",", "") + "\n";

			// File summaryFile = new
			// File(FilenameUtils.getFullPath(listOfFiles[i].toString()) +
			// listOfFiles[i].getName() + "-automatic.txt");
			try {
				FileUtils.write(summaryFile, outputFileContent, true);
			} catch (IOException e) {
				logger.info("Error in writing summary");
			}
			outputFileContent = "";
		}
		// System.out.println(outputFileContent);
		/*
		 * try { FileUtils.write(summaryFile, outputFileContent); } catch
		 * (IOException e) { logger.info("Error in writing summary"); }
		 */
		Instant end = Instant.now();
		System.out.println("Completion time: " + Duration.between(start, end));
	}

	public static String removeGarbage(String sentence) {
		sentence.replaceAll("[^\\p{ASCII}]", ""); // Strips off non-ascii
													// characters
		sentence = sentence.replaceAll("\\s+", " ");
		sentence = sentence.replaceAll("\\p{Cntrl}", ""); // Strips off ascii
															// control
															// characters
		sentence = sentence.replaceAll("[^\\p{Print}]", ""); // Strips off ascii
																// non-printable
																// characters
		sentence = sentence.replaceAll("\\p{C}", ""); // Strips off
														// non-printable
														// characters from
														// unicode
		return sentence;
	}// end method
}// end class