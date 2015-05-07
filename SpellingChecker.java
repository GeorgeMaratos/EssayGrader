//refrence: http://www.javacodegeeks.com/2010/05/did-you-mean-feature-lucene-spell.html
package cs421nlp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;



public class SpellingChecker {
	
	private static Vector<String> backupDictionairy = new Vector<String>();
	public static WordNetDatabase database;
	
	public SpellingChecker() throws IOException {
		System.out.println("Building Dictionairy");
		String s;
		BufferedReader in
		   = new BufferedReader(
				   new FileReader("src/spellchecker/otherDict.txt"));

		s = in.readLine();
		while(s != null) {
			backupDictionairy.add(s);
			s = in.readLine();
		}
		System.out.println("Done");
		
		System.setProperty("wordnet.database.dir", "src/WordNet-3.0/dict");
		database = WordNetDatabase.getFileInstance();
		in.close();
	}
	//high: 0.5, .45, =1.1333333333333333, 0.375, 0.6428571428571429, 0.6428571428571429, .5, .41, 1.06. 
			//high total avg: .588
			//low 1.4545454545454546, 0.46153846153846156, 0.4666666666666667, 1.1875, 0.8888888888888888, 0.4117647058823529, 5.142857142857143
			//3.142857142857143, 0.375
			//low total avg:  1.4364951796569445
			
			//medium total avg:1.0083044509669896
			//1.2 .6 .6 .7 2.4,
	public static int eval(String essay,boolean debug){
		int mistakes=checkMistakes(essay.split("[\\.\\,\\s]+"),debug);
		int sentences=SentenceSplitter.split(essay,debug).length;
		double percentage =(double)mistakes/sentences;
		if(percentage<.4){
			return 5;
		}else if(percentage<.8){
			return 4;
		}else if(percentage<1.2){
			return 3;
		}else if(percentage<2.0){
			return 2;
		}else{
			return 1;
		}
		//return percentage;
		
	}
	public static int countMistakes(String essay, boolean debug){
	
		return checkMistakes(essay.split("[\\;\\.\\,\\s]+"),debug);
	}
	
	private static int checkMistakes(String[] words,boolean debug){

		int spellingErrors = 0;
		for(int i = 0; i < words.length;i++) {
			//check if numeric or non-verb,adj,adv,vrb
			if(!words[i].matches("-?\\d+(\\.\\d+)?")&&!backupDictionairy.contains(words[i].toLowerCase())) {
				if(words==null){
					return spellingErrors;
				}
				Synset[] s = database.getSynsets(words[i].toLowerCase());
				//database.
				if(s.length == 0) {
					spellingErrors++;
					if(debug) System.out.println(words[i]);
				}
			}
		}
		
		return spellingErrors;
	}
	
}
