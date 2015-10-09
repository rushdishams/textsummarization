package com.boundary.sentence;

import java.util.ArrayList;
import java.util.List;

import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

/**
 * @author Rushdi Shams, UWO, Canada
 * @version 1.0	30/10/2012
 * This class describes a text object. The whole string text is considered as an object.
 * The object has 4 methods: (1) to set the string content of text, (2) to return the content of the text as string
 * (3) to set the boundary of each sentence in a text with lingpipe model (4) to return the sentences of a text as a 
 * string array
 */


public class TextContent {
	
	//Instance variable
	static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;//lingpipe object
    static final SentenceModel SENTENCE_MODEL  = new MedlineSentenceModel();//lingpipe object
    private String content ="";
    private String[] sBarray;
    
    //Constructor
    /**
     * @return nothing.
     * @category constructor method.
     */
    public TextContent (){
    }
    
    //setting the text
    /**
     * @return void.
     * @category set method.
     * @param String. The whole text file.
     */
	public void setText(String text){
    	this.content = text;
    }
	
	//returns the content of the text
	/**
     * @return String. Returns the text file associated with the object.
     * @category get method.
     * @param Nothing.
     */
	public String getText(){
		return this.content;
	}
	
	//setting boundary marking for each sentence in the text
	/**
     * @return void.
     * @category set method. Uses the lingpipe sentence boundary detection method to identify sentence boundaries.
     * @param Nothing.
     */
    public void setSentenceBoundary (){
		
		//initializing lingpipe sentence boundary marking
		List<String> tokenList = new ArrayList<String>();
		List<String> whiteList = new ArrayList<String>();
		String temp = this.content.trim();
		Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(temp.toCharArray(),0,temp.length());
		
		tokenizer.tokenize(tokenList,whiteList);
		
		String[] tokens = new String[tokenList.size()];
		String[] whites = new String[whiteList.size()];
		
		tokenList.toArray(tokens);
		whiteList.toArray(whites);
		
		int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens,whites);
		
		//taking the number of sentences
		int boundaryLength = sentenceBoundaries.length;
		
		//array to hold every sentence of a document
		this.sBarray = new String[boundaryLength];
					
		int sentStartTok = 0;
		int sentEndTok = 0;
		
		for (int u = 0; u < boundaryLength; u++) {
		
			//temporarily holds sentences
			StringBuilder s = new StringBuilder();
			
		    sentEndTok = sentenceBoundaries[u];

		    for (int v = sentStartTok; v <= sentEndTok; v++) {
		    	
		    	s.append(tokens[v].trim() + whites[v + 1]);
		    	
		    }//for (int v = sentStartTok; v <= sentEndTok; v++)
		    
		    //taking sentences to the sentence array
		    this.sBarray[u] = s.toString().trim();
		    
		    sentStartTok = sentEndTok + 1;
		    
		}//for (int u = 0; u < sentenceBoundaries.length; u++)
		
	}//public void setSentenceBoundary ()
    
    //returns the sentences of a text in an array
    /**
     * @return String array. The array contains all the sentences of the text object.
     * @category get method
     * @param Nothing.
     */
    public String[] getSentence(){
    	return this.sBarray;
    }//public String[] getSentence()
    
}//public class TextContent
