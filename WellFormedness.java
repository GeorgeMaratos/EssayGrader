package cs421nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.regex.Pattern;

import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.util.InvalidFormatException;

public class WellFormedness {
//	private String[] verbList = {"VB" , "VBG" , "VBN", "VBZ", "VBP", "VBD" };
	private String[] nounList = { "PRP", "NP", "NN" , "NNP", "NNS", "NNPS" };
	private Parser parser = null;
	private String[] essay = null;
	private Vector<String[]> tags = null;
	private int errors = 0;

	
	public WellFormedness(Parser parser) throws IOException {
		
		//System.out.println("Preparing to check for wellformedness");
		tags = new Vector<String[]>();
		this.parser=parser;
		/*InputStream is = new FileInputStream("src/spellchecker/en-parser-chunking.bin");		 
		ParserModel model = new ParserModel(is); 
		parser = ParserFactory.create(model);
		is.close();
		OutputFormatter.init();	*/
	}
	
	
	private String Format(String s) {
		s = s.replaceAll(Pattern. quote(","), " ,");
		s = s.replaceAll(Pattern. quote(";"), " ;");
		return s;
	}
	
	private boolean checkMultiClause(int index, boolean debug) {
		String[] sentenceFragments = tags.get(index);
		Vector<Integer> commaIndex = new Vector<Integer>();	
		String IN = "IN";
		String WRB = "WRB";
		
		boolean foundDependence = false;
		int i = 0, commaLoc = -1;
		int commaCount = 0;
		while(i < sentenceFragments.length) {
			if(sentenceFragments[i].equals(",")) {
				commaLoc = i;
				commaCount++;
			}
			i++;
		}
		
		if(commaCount == 2) {
			for(int j=0;j<commaLoc;j++) {
				if(sentenceFragments[j].equals(IN) || sentenceFragments[j].equals(WRB)) {
					if(debug) System.out.println("Dependence Detected: " + sentenceFragments[j] + " " + j);
					foundDependence = true;
				}
				if(foundDependence) {
					for(int k=0;k<nounList.length;k++) {
						if(sentenceFragments[j].contentEquals(nounList[k]))
							foundDependence = true;
					}
				}
			}
			
			//outside of first half
			if(!foundDependence) {
				if(debug) System.out.println("There is an independent clause before: " + commaLoc);
				return false;
			}
			
			foundDependence = false;
			
			for(int j=commaLoc;j<sentenceFragments.length;j++) {
				if(sentenceFragments[j].equals(IN) || sentenceFragments[j].equals(WRB)) {
					if(debug) System.out.println("Dependence Detected: " + sentenceFragments[j] + " " + j);
					foundDependence = true;
				}
				if(foundDependence) {
					for(int k=0;k<nounList.length;k++) {
						if(sentenceFragments[j].contentEquals(nounList[k]))
							foundDependence = true;
					}
				}
			}
			if(!foundDependence) {
				if(debug) System.out.println("There is an independent clause before: " + commaLoc);
				return false;
			}
			return true;
		}
		if(debug) System.out.println("Comma: " + commaLoc);
		return false;
	}
	
	private boolean isDependent(int index, boolean debug) {
		boolean foundDependence = false;
		String IN = "IN";
		String WRB = "WRB";
		String[] localTags = tags.get(index);
		//check for either of these before the Noun
		for(int i=0;i<localTags.length;i++) {
			if(localTags[i].equals(IN) || localTags[i].equals(WRB)) {
				if(debug) System.out.println("Dependence Detected: " + localTags[i] + " " + i);
				foundDependence = true;
			}
			if(foundDependence) {
				for(int j=0;j<nounList.length;j++) {
					if(localTags[i].contentEquals(nounList[j]))
						return true;
				}
			}
			
		}
		return false;
	}
	
	private boolean isNotWellFormed(int index, boolean debug) {
		//checks through tags for comma or semicolon
		String[] s = essay[index].split("[,;]");
		if(debug) System.out.println("Number of Clauses: " + s.length);
		switch(s.length) {
		case 1:
			return isDependent(index, debug);
			
		default:
			//this is the check for dangling words
			int length1 = s[0].split(" ").length;
			int length2 = s[s.length - 1].split(" ").length;
			if(length1 == 1 || length2 == 2)
				return true;
			//then check the multi-clause
			else return checkMultiClause(index,false);
			
		}
		
	}
	public int feedEssay(String essay1, boolean debug) {
		if(debug) System.out.println("Feeding essay");
		int i;
		essay = SentenceSplitter.split(essay1, false);
		tags.clear();
		errors = 0;
		for(i=0;i<essay.length;i++) {
			essay[i] = Format(essay[i]);
			tags.add(SentenceTagger.getTags(essay[i], parser));
		}
		//here is where it comes together, evaluate clauses to determine if the sentence is correct
		for(i=0;i<essay.length;i++) {
			//for all the sentences		
			//check if word is by itself
			if(isNotWellFormed(i,debug)) {
				if(debug) System.out.println("Is not Wellformed: " + i);
				errors++;
			}
		}
		if(debug) {
			
			for(String[] e:tags) {
				for(String s:e)
					System.out.print(s + " ");
			}
			System.out.println();
		}
		double percent= (double)errors/essay.length;
		if(percent<.3){
			return 5;
		}else if(percent<.45){
			return 4;
		}else if(percent<.6){
			return 3;
		}else if(percent<.75){
			return 2;
		}else{
			return 1;
		}
	}
	
}
