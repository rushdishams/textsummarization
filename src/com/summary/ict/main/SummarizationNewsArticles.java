package com.summary.ict.main;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.boundary.sentence.TextContent;

import net.sf.classifier4J.summariser.ISummariser;
import net.sf.classifier4J.summariser.SimpleSummariser;

/**
 * The class to instantiate Simple Summariser object. Simple Summariser can be
 * found in Classify4j project.
 * 
 * It takes all articles in String format separated by a delimiter and generates
 * x% summary for the articles in the same order separated by the same
 * delimiter. The x is supplied by the user from the console.
 * 
 * 
 * @author Rushdi Shams
 * @version 0.1.0 December 18, 2015.
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
			e.printStackTrace();
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


	public static void main(String[] args) {

		Instant start = Instant.now();

		String articlesFromInput = args[0];
		articlesFromInput = removeGarbage(articlesFromInput);
		float summarySize = Float.parseFloat(args[1]);
		List<String> articleContents = new ArrayList<String>(); 
				articleContents = getTagValues(articlesFromInput);

		String aggregatedSummaries = "";


		int i = 1;
		for (String article : articleContents) {

			if (article.length() == 0) {
				continue;
			}

			TextContent t = new TextContent(); // creating TextContent object
			t.setText(article);
			t.setSentenceBoundary();
			String[] content = t.getSentence();

			int articleSummaryLength = Math.round((float) content.length * summarySize);

			String summary = summarize(article, articleSummaryLength).trim();

			aggregatedSummaries += "<summary>" + summary + "</summary>" + "\n";

			i++;

		}

		Instant end = Instant.now();
		System.out.println(aggregatedSummaries);
	}

}// end class