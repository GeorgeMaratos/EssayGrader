package cs421nlp;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.parser.Parser;

public class Verbeval {
	public static int evalEssay(String essay, Parser parser, boolean debug) throws IOException{
		
		String sentences[]=essay.split(Pattern.quote("."));
		int errors=0;
		int numsent=0;
		for(String sent:sentences){
			//System.out.println(sent);
			errors+=Verbeval.evalSentence(sent,parser, debug);
			numsent++;
		}
		double percentage=(double)errors/numsent;
		if(debug) System.out.println("number of errors "+errors+ " number of sentences= "+numsent+ " percentage: "+percentage);
		
		if(percentage<.25){
			return 5;
		}else if(percentage<.4){
			return 4;
		}else if(percentage<.55){
			return 3;
		}else if(percentage<.6){
			return 2;
		}else{
			return 1;
		}
		//return percentage;
	}
	public static int evalSentence(String essay,Parser parser, boolean debug) throws IOException {
		//System.out.println("gets here");
		String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(essay);
		//String[] tags = parser.tag(whitespaceTokenizerLine);
		String[] tags=SentenceTagger.getTags(essay, parser);
		
		if(debug){
			for(String tag:tags){
				System.out.println(tag);
			}
		}
		
		
		int curr=0;
		int errors=0;
		
		boolean hasMainVerb=false;
		//check for missing vebs:
		for(int i=0;i<tags.length;i++){
			if(tags[i].equals("VBP")||tags[i].equals("VBZ")||tags[i].equals("VBD")){
				hasMainVerb=true;
			}else if(tags[i].equals("VB")){
				int before=i-1;
				curr=i+1;
				if(before>=0){
					if(tags[before].equals("MD")){
						hasMainVerb=true;
					}
				}
			}
		}
		if(!hasMainVerb){
			if(debug) System.out.println("doesn't have main verb:");
			errors++;
		}
	
		
		//check for no combos of VBG,VBZ,VB,VBD,VBP
		for(int i=0;i<tags.length;i++){
			if(tags[i].equals("VBP")||tags[i].equals("VBZ")||tags[i].equals("VBD")||tags[i].equals("VB")||tags[i].equals("VBN")||tags[i].equals("VBG")){
				if(debug) System.out.println("found verb: "+whitespaceTokenizerLine[i]);
				if(tags[i].equals("VBP")||tags[i].equals("VBZ")||tags[i].equals("VBD")){
					if(i!=tags.length-1){
						curr=i+1;
						if(tags[curr].equals("VBG")||tags[curr].equals("VBN")){
							if(debug) System.out.println("no error: followed by VBN or VBG: "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[curr]);
							//errors++;
						}else if(tags[curr].equals("VBP")||tags[curr].equals("VBD")||tags[curr].equals("VBZ")||tags[curr].equals("VB")){
							if(debug) System.out.println("error: followed by VBD, VDZ, VBP: "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[curr]);
							errors++;
						}
					}
				}else if(tags[i].equals("VBG")||tags[i].equals("VBN")){
					curr=i+1;
					if(i<tags.length-1){
						if(tags[curr].equals("VBP")||tags[curr].equals("VBZ")||tags[curr].equals("VBD")||tags[curr].equals("VB")||tags[curr].equals("VBN")||tags[curr].equals("VBG")){
							if(debug)System.out.println("error, VBG/VBN followed by verb"+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[curr] );
							errors++;
						}
					}
				}else if(tags[i].equals("VB")){
					int before=i-1;
					curr=i+1;
					if(before>=0){
						if(tags[before].equals("MD")||tags[before].equals("TO")){
							if(debug)System.out.println("no error, MD VB "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[before] );
						}else{
							if(debug)System.out.println("error, no MD before VB "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[before] );
							errors++;
						}
					}
					if(i!=tags.length-1){
						curr=i+1;
						if(tags[curr].equals("VBG")||tags[curr].equals("VBN")){
							if(debug) System.out.println("no error: followed by VBN or VBG: "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[curr]);
							//errors++;
						}else if(tags[curr].equals("VBP")||tags[curr].equals("VBD")||tags[curr].equals("VBZ")||tags[curr].equals("VB")){
							if(debug) System.out.println("error: followed by VBD, VDZ, VBP: "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[curr]);
							errors++;
						}
					}
				}
			}
		}
		
		
		return errors;
	}
	
}
//check for is/are VBG, by finding VBG, then checkin for is/are, make sure VBG isn't started by other verbs
		/*for(int i=0;i<tags.length;i++){
			if(tags[i].equals("VBG")||tags[i].equals("VBN")){
				if(debug) System.out.println("VBG is "+whitespaceTokenizerLine[i]);
				curr=i;
				while(curr-->0){
					if(i==0){
						if(debug) System.out.println("no error, first word of sentence: "+whitespaceTokenizerLine[i]);
						break;
					}
					if(whitespaceTokenizerLine[curr].compareToIgnoreCase("is")==0||
							whitespaceTokenizerLine[curr].compareToIgnoreCase("are")==0||
							whitespaceTokenizerLine[curr].compareToIgnoreCase("were")==0||
							whitespaceTokenizerLine[curr].compareToIgnoreCase("was")==0||
							whitespaceTokenizerLine[curr].compareToIgnoreCase("am")==0){
						if(debug) System.out.println("no error: "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[curr]);
						break;
					}else if(tags[curr].equals("VBZ")||tags[curr].equals("VBP")||tags[curr].equals("VBD")||tags[curr].equals("VB")){
						if(debug) System.out.println("Verb was good: "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[curr]);
						break;
					}
				}
				if(curr==-1&&i!=0){
					if(debug) System.out.println("error: "+whitespaceTokenizerLine[i]);
					errors++;
				}
			}
		}*/

		/*
		//find -ing verbs
	String[] ingwords={"avoid","deny","finish","regret", "be", "dislike", "imagine", "risk",
				"n’t", "help", "mind", "to",  "enjoy", "practice", "stop",
				"consider", "like", "recommend", "suggest", "of"};
		for(int i=0;i<tags.length;i++){
			if(tags[i].equals("VBG")){
				if(debug) System.out.println("VBG is "+whitespaceTokenizerLine[i]);
				curr=i;
				while(curr-->0){
					if(i==0){
						if(debug) System.out.println("no error, first word of sentence: "+whitespaceTokenizerLine[i]);
						break;
					}else if(whitespaceTokenizerLine[curr].compareToIgnoreCase("is")==0||
							whitespaceTokenizerLine[curr].compareToIgnoreCase("are")==0||
							whitespaceTokenizerLine[curr].compareToIgnoreCase("were")==0||
							whitespaceTokenizerLine[curr].compareToIgnoreCase("was")==0||
							whitespaceTokenizerLine[curr].compareToIgnoreCase("am")==0){
						if(debug) System.out.println("found to be verb: "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[curr] );
						break;
					}else{
						for(String ingword:ingwords){
							if(whitespaceTokenizerLine[curr].compareToIgnoreCase(ingword)==0){
								if(debug) System.out.println("found good word: "+whitespaceTokenizerLine[i]+" "+whitespaceTokenizerLine[curr] );
								break;
							}
						}
					}
				}
				if(curr==-1&&i!=0){
					if(debug) System.out.println("error: "+whitespaceTokenizerLine[i]);
					errors++;
				}
				
			}
		}*/
