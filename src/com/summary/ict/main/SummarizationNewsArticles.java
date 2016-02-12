package com.summary.ict.main;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.boundary.sentence.TextContent;

import net.sf.classifier4J.summariser.ISummariser;
import net.sf.classifier4J.summariser.SimpleSummariser;

/**
 * The class to instantiate Simple Summariser object. Simple Summariser can be
 * found in Classify4j project.
 * 
 * It takes an input file and output file and generates x% summary for the
 * articles in the same order. The x is supplied by the user from the console.
 * The input file has each article content within <article>..</article> tags.
 * The output file will contain summaries for the correspoinding articles in
 * <summary>..</summary> tags.
 * 
 * 
 * @author Rushdi Shams
 * @version 0.8.0 February 12, 2016.
 * 
 *          Change: + 0.6.0 version except garbage removal--there is no garbage removal at all
 *
 */
public class SummarizationNewsArticles {
	// --------------------------------------------------------------------------------
	// Instance variables
	// --------------------------------------------------------------------------------
	private static String summaryText;
	private static ISummariser summarizer;
	// --------------------------------------------------------------------------------
	// Methods
	// --------------------------------------------------------------------------------

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
		StringUtils.replace(sentence, "[^\\p{ASCII}]", ""); // non-ASCII
		StringUtils.replace(sentence, "\\s+", " "); // recurring whitespace
		StringUtils.replace(sentence, "\\p{Cntrl}", "");// Control
		StringUtils.replace(sentence, "[^\\p{Print}]", ""); // Non-printable
		StringUtils.replace(sentence, "\\p{C}", ""); // non-printable for
														// unicode

		return sentence;
	}// end method

	/**
	 * Method to record the summaries in output file
	 * 
	 * @param outputFile
	 * @param content
	 */
	public static void writeSummaries(String outputFile, String content) {
		try {
			FileUtils.write(new File(outputFile), content, "UTF-8", true);
		} catch (IOException e) {
			System.out.println("Cannot write summaries");
		}
	}

	/**
	 * Driver method
	 * 
	 * @param args
	 *            input file, output file, summary size
	 */
	public static void main(String[] args) {

		Instant start = Instant.now();

		String articlesFromInput = "";
		System.out.println("-- Reading Input File --");
		try {
			articlesFromInput = FileUtils.readFileToString(new File(args[0]), "UTF-8");
		} catch (IOException e) {
			System.out.println("-- Cannot parse input file --");
		}
		System.out.println("-- Done Reading Input File --");

		float summarySize = Float.parseFloat(args[2]);
		String[] tagContents = StringUtils.substringsBetween(articlesFromInput, "<article>", "</article");

		List<String> articleContents = Arrays.asList(tagContents);

		// StringBuilder aggregatedSummaries = new StringBuilder();
		System.out.println("-- Let's Process the Articles --");
		int articleNumber = 1;
		for (String article : articleContents) {

//			article = removeGarbage(article); // remove garbage for current
//												// article
			System.out.println("Processing article " + articleNumber + "/" + articleContents.size());

			/* Getting sentences from the current article */
			TextContent t = new TextContent();
			t.setText(article);
			t.setSentenceBoundary();

			String[] content = t.getSentence();// content has the sentences

			/*
			 * In case there is no sentence in the article, write empty string
			 * in the output file
			 */
			if (content.length == 0) {
				// aggregatedSummaries.append("<summary>" + "" + "</summary>" +
				// "\n");
				articleNumber++;
				writeSummaries(args[1], "<summary>" + "" + "</summary>" + "\n");
				continue;
			}
			/*
			 * In case there is one sentence in the article, write write that
			 * sentence in the output file
			 */
			if (content.length == 1) {
				// aggregatedSummaries.append("<summary>" + content[0] +
				// "</summary>" + "\n");
				articleNumber++;
				writeSummaries(args[1], "<summary>" + content[0] + "</summary>" + "\n");
				continue;
			}

			/* Generating summaries using Classifier4j */
			int articleSummaryLength = Math.round((float) content.length * summarySize);
			String summary = summarize(article, articleSummaryLength).trim();

			// aggregatedSummaries.append("<summary>" + summary + "</summary>" +
			// "\n");
			writeSummaries(args[1], "<summary>" + summary + "</summary>" + "\n"); 
			articleNumber++;
		} // let's move to the second article

		// writeSummaries(args[1], aggregatedSummaries.toString());
		Instant end = Instant.now();
		System.out.println(Duration.between(start, end));
	}// end driver method

}// end class