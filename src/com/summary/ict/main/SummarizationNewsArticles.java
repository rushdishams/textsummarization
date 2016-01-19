package com.summary.ict.main;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.boundary.sentence.TextContent;

import net.sf.classifier4J.summariser.ISummariser;
import net.sf.classifier4J.summariser.SimpleSummariser;

/**
 * The class to instantiate Simple Summariser object. Simple Summariser can be
 * found in Classify4j project.
 * 
 * It takes an input file and output file and generates
 * x% summary for the articles in the same order. The x is supplied by the user from the console.
 * The input file has each article content within <article>..</article> tags. The output file
 * will contain summaries for the correspoinding articles in <summary>..</summary> tags. 
 * 
 * 
 * @author Rushdi Shams
 * @version 0.4.0 January 19, 2016.
 * 
 * Change:
 * StringBuilder is used whenever strings are concatenated. 
 *
 */
public class SummarizationNewsArticles {
	// --------------------------------------------------------------------------------
	// Instance variables
	// --------------------------------------------------------------------------------
	private static String summaryText;
	private static ISummariser summarizer;
	private static final Pattern TAG_REGEX = Pattern.compile("<article>(.+?)</article>");
	// --------------------------------------------------------------------------------
	// Methods
	// --------------------------------------------------------------------------------

	private static List<String> getTagValues(final String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = TAG_REGEX.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1));
	    }
	    return tagValues;
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
			System.out.println("Error while summarizing");
		}
		return summaryText;
	}// end method

	public static String removeGarbage(String sentence) {
		sentence = sentence.replaceAll("[^\\p{ASCII}]", ""); // Strips off non-ascii
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
	
	public static void writeSummaries(String outputFile, String content){
		try {
			FileUtils.write(new File(outputFile), content);
		} catch (IOException e) {
			System.out.println("Cannot write summaries");
		}
	}


	public static void main(String[] args) {

		Instant start = Instant.now();

		String articlesFromInput = "";
		try {
			articlesFromInput = FileUtils.readFileToString(new File(args[0]));
		} catch (IOException e) {
			System.out.println("Cannot parse input file");
		}
		articlesFromInput = removeGarbage(articlesFromInput);
		float summarySize = Float.parseFloat(args[2]);
		List<String> articleContents = getTagValues(articlesFromInput);

		StringBuilder aggregatedSummaries = new StringBuilder();

		int articleNumber = 1;
		TextContent t = new TextContent(); // creating TextContent object
		for (String article : articleContents) {

			System.out.println("Processing article " + articleNumber + "/" + articleContents.size());
			t.setText(article);
			t.setSentenceBoundary();
			String[] content = t.getSentence();
			if(content.length == 0){
				aggregatedSummaries.append("<summary>" + "" + "</summary>" + "\n");
				articleNumber++;
				continue;
			}
			if(content.length == 1){
				aggregatedSummaries.append("<summary>" + content[0] + "</summary>" + "\n");
				articleNumber++;
				continue;
			}

			int articleSummaryLength = Math.round((float) content.length * summarySize);

			String summary = summarize(article, articleSummaryLength).trim();

			aggregatedSummaries.append("<summary>" + summary + "</summary>" + "\n");
			articleNumber++;
		}

		writeSummaries(args[1], aggregatedSummaries.toString());
		Instant end = Instant.now();
		System.out.println(Duration.between(start, end));
	}

}// end class