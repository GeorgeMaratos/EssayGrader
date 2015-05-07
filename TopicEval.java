package cs421nlp;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.smu.tspell.wordnet.Synset;

public class TopicEval {
	private static String[] wordList={
		"car","automobile","gas","prices","expensive","gasoline","polution","accident","vehicle", "transportation", "technology","cars", "years", "progress", "development" 
	};
	private static Vector<String> wordVect=null;
	public static void init(){
		wordVect=new Vector<String>();
		for(String word:wordList){
			Synset[] s = SpellingChecker.database.getSynsets(word.toLowerCase());
			for(Synset set:s){
				wordVect.addAll(Arrays.asList(set.getWordForms()));
				wordVect.add(word);
			}
		}
		
	}
	public static int eval(String essay, boolean debug){
		int count=0;
		Pattern pattern = Pattern.compile("\\w+");
		Matcher matcher = pattern.matcher(essay);
		while (matcher.find()) {
			if(wordVect.contains(matcher.group().toLowerCase())){
				count++;
			}
		}
		int numSent=SentenceSplitter.split(essay, false).length;
		double percent= (double)count/numSent;
		if(percent<.8){
			return 1;
		}else if(percent<1){
			return 2;
		}else if(percent<1.2){
			return 3;
		}else if(percent<1.5){
			return 4;
		}else{
			return 5;
		}
	}
}
