package cs421nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;

public class SentenceSplitter {
	private static SentenceDetectorME sentenceDetector;
	public static void init(SentenceDetectorME sentenceDetector){
		SentenceSplitter.sentenceDetector=sentenceDetector;
	}
	public static String[] split(String essay,boolean debug){
		/*String sentences[] = sentenceDetector.sentDetect(essay);
		if(debug){
			for(String tag: sentences){
				System.out.println(tag);
			}
		}*/
		String sentences[]=essay.split("[.?]");
		return sentences;
	}
	public static int eval(String essay){
		int num=split(essay,false).length;
		if(num<10){
			return 1;
		}else if(num<12){
			return 2;
		}else if(num<15){
			return 3;
		}else if(num<17){
			return 4;
		}else{
			return 5;
		}
	}
}
