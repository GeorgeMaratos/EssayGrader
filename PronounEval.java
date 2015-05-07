package cs421nlp;

import opennlp.tools.parser.Parser;
import opennlp.tools.parser.Parse;

public class PronounEval {
	static Parser parser;
	public static void init(Parser parser){
		PronounEval.parser=parser;
	}
	public static int eval(String essay, boolean debug){
		String[] sentences=SentenceSplitter.split(essay,false);
		int errors=0;
		boolean foundSing=false,foundPlural=false;
		for(int i=0;i<sentences.length-1;i++){
			
			//find entities in prev sentence, and next sentence, then find pronouns in next sentence
			boolean antsbools[]=findAnts(sentences[i],debug);
			foundSing=antsbools[0];
			foundPlural=antsbools[1];
			
			Parse tags[]=SentenceTagger.getParseNodes(sentences[i+1], parser);
			
			//check next sentence to see if they reference them
			for(int j=0;j<tags.length;j++){
				if(tags[j].getType().equals("PRP")){
					if(tags[j].getCoveredText().compareToIgnoreCase("he")==0||tags[j].getCoveredText().compareToIgnoreCase("she")==0||tags[j].getCoveredText().compareToIgnoreCase("him")==0||tags[j].getCoveredText().compareToIgnoreCase("her")==0||tags[j].getCoveredText().compareToIgnoreCase("it")==0){
						if(!foundSing){
							if(!checkifmentionedbefore(tags, j, true)){
								errors++;
								if(debug) System.out.println("bad pronoun antecedent "+tags[j].getCoveredText()+" sentence #"+i+": "+sentences[i+1]);
								
							}
						}
					}
					if(tags[j].getCoveredText().compareToIgnoreCase("they")==0||tags[j].getCoveredText().compareToIgnoreCase("them")==0){
						if(!foundPlural){
							if(!checkifmentionedbefore(tags, j, false)){
								errors++;
								if(debug) System.out.println("bad pronoun antecedent "+tags[j].getCoveredText()+" sentence #"+i+": "+sentences[i+1]);
									
							}
						
						}
					}
				}
				if(tags[j].getType().equals("PRP$")){
					if(tags[j].getCoveredText().compareToIgnoreCase("his")==0||tags[j].getCoveredText().compareToIgnoreCase("her")==0||tags[j].getCoveredText().compareToIgnoreCase("its")==0){
						if(!foundSing){
							if(!checkifmentionedbefore(tags, j, true)){
									errors++;
									if(debug) System.out.println("bad pronoun antecedent "+tags[j].getCoveredText()+" sentence #"+i+": "+sentences[i+1]);
									
							}	
							
						}
					}
					if(tags[j].getCoveredText().compareToIgnoreCase("their")==0){
						if(!foundPlural){
							if(!checkifmentionedbefore(tags, j, false)){
								errors++;
								if(debug) System.out.println("bad pronoun antecedent "+tags[j].getCoveredText()+" sentence #"+i+": "+sentences[i+1]);
								
							}
							
						}
					}
				}
			}
			foundSing=false;
			foundPlural=false;
		}
		if(errors<5){
			return 5-errors;
		}else{
			return 0;
		}
		//return errors;
		
		
	}
	public static boolean[] findAnts(String sentence, boolean debug){
		boolean ret[]=new boolean[2];
		boolean foundSing=false,foundPlural=false;
		Parse tags[]=SentenceTagger.getParseNodes(sentence, parser);
		for(int j=0;j<tags.length;j++){
			if(tags[j].getType().equals("NN")||tags[j].getType().equals("NNP")){
				foundSing=true;
				
				//if(debug) System.out.println("found singular "+tags[j].getCoveredText()+" sentence:"+sentence);
				break;
			}
			if(tags[j].getType().equals("NNS")||tags[j].getType().equals("NNPS")){
				foundPlural=true;
				//if(debug) System.out.println("found plural "+tags[j].getCoveredText()+" sentence:"+sentence);
				break;
			}
			/*if(tags[j].getType().equals("PRP")){
				if(tags[j].getCoveredText().compareToIgnoreCase("he")==0||tags[j].getCoveredText().compareToIgnoreCase("she")==0||tags[j].getCoveredText().compareToIgnoreCase("her")==0||tags[j].getCoveredText().compareToIgnoreCase("him")==0||tags[j].getCoveredText().compareToIgnoreCase("it")==0){
					foundSing=true;
					if(debug) System.out.println("found singular "+tags[j].getCoveredText()+" sentence:"+sentence);
				}
				if(tags[j].getCoveredText().compareToIgnoreCase("they")==0||tags[j].getCoveredText().compareToIgnoreCase("them")==0){
					foundPlural=true;
					if(debug) System.out.println("found plural "+tags[j].getCoveredText()+" sentence:"+sentence);
				}
			}
			if(tags[j].getType().equals("PRP$")){
				if(tags[j].getCoveredText().compareToIgnoreCase("his")==0||tags[j].getCoveredText().compareToIgnoreCase("her")==0||tags[j].getCoveredText().compareToIgnoreCase("its")==0){
					foundSing=true;
					if(debug) System.out.println("found singular "+tags[j].getCoveredText()+" sentence:"+sentence);
				}
				if(tags[j].getCoveredText().compareToIgnoreCase("their")==0){
					foundPlural=true;
					if(debug) System.out.println("found plural "+tags[j].getCoveredText()+" sentence:"+sentence);
				}
			}*/
		}	
		ret[0]=foundSing;
		ret[1]=foundPlural;
		return ret;
		
	}
	public static boolean checkifmentionedbefore(Parse[] tags, int start, boolean isSingular){
		boolean foundSing=false,foundPlural=false;
		for(int i=start-1; i>0;i--){
			if(tags[i].getType().equals("NN")||tags[i].getType().equals("NNP")){
				foundSing=true;
			}
			if(tags[i].getType().equals("NNS")||tags[i].getType().equals("NNPS")){
				foundPlural=true;
			}
			/*if(tags[i].getType().equals("PRP")){
				if(tags[i].getCoveredText().compareToIgnoreCase("he")==0||tags[i].getCoveredText().compareToIgnoreCase("she")==0||tags[i].getCoveredText().compareToIgnoreCase("her")==0||tags[i].getCoveredText().compareToIgnoreCase("him")==0||tags[i].getCoveredText().compareToIgnoreCase("it")==0){
					foundSing=true;
				}
				if(tags[i].getCoveredText().compareToIgnoreCase("they")==0||tags[i].getCoveredText().compareToIgnoreCase("them")==0){
					foundPlural=true;
				}
			}
			if(tags[i].getType().equals("PRP$")){
				if(tags[i].getCoveredText().compareToIgnoreCase("his")==0||tags[i].getCoveredText().compareToIgnoreCase("her")==0||tags[i].getCoveredText().compareToIgnoreCase("its")==0){
					foundSing=true;
				}
				if(tags[i].getCoveredText().compareToIgnoreCase("their")==0){
					foundPlural=true;
				}
			}*/
			
		}
		if(isSingular){
			return foundSing;
		}else{
			return foundPlural;
		}
		
	}
}
