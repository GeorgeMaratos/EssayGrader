package cs421nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.util.InvalidFormatException;

public class SVEval {
	private static Parser parser=null;
	public static void init(Parser parser) throws InvalidFormatException, IOException{
		SVEval.parser=parser;
	}
	public static int eval(String essay,boolean debug){
		String[] sentences=SentenceSplitter.split(essay,debug);
		int errors=0;
		for(String sentence:sentences){
			errors+=evalSent(sentence,debug);
		}
		double percentage=(double)errors/sentences.length;
		if(percentage<.05){
			return 5;
		}else if(percentage<.1){
			return 4;
		}else if(percentage<.15){
			return 3;
		}else if(percentage<.2){
			return 2;
		}else{
			return 1;
		}
		//return percentage;
		
	}
	
	public static int evalSent(String sentence,boolean debug){
		//String sentence = "People in old industrial countries have their own cars but people in Asia or India do not have their own cars and if they earn the money they would like to buy a new car.";
		if(sentence.trim().length()<3){
			return 0;
		}
		Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
		if(topParses.length<=0){
			return 0;
		}
		Parse p=topParses[0];
		StringBuffer sb=new StringBuffer();
		p.show(sb);
		if(debug) System.out.println(sb);
		int errors=checkSentSV(p,debug,new ArrayList<Parse>());
		if(debug) System.out.println("errors: "+errors);
		return errors;
	}

	public static int checkSentSV(Parse p, boolean debug, ArrayList<Parse> covered){

		//check if there's a NP and VP in child
		int errors=0;
		Parse np=null,vp=null;
		for(Parse child:p.getChildren()){
			if(child.getType().equals("NP")){
				np=child;
			}else if(child.getType().equals("VP")){
				vp=child;
			}
			/*if(np==null){
				np=getNP(child);
			}
			if(vp==null){
				vp=getVP(child);
			}*/
			
		}
		if(vp!=null&&np!=null){
			if(debug) System.out.println("found vp and np");
			if(debug) System.out.println("showing main noun:");
			Parse noun=getMainNoun(np,covered);
			if(noun!=null){
				if(debug) noun.show();
			}else{
				if(debug) System.out.println("not found");
			}
			if(debug) System.out.println("showing main verb:");
			Parse verb=getMainVerb(vp,covered);
			if(verb!=null){
				if(debug) verb.show();
			}else{
				if(debug) System.out.println("not found");
			}
			if(verb!=null&&noun!=null){
				if(noun.getType().equals("WDT")||noun.getType().equals("WP")){
					if(debug) System.out.println("no error found: "+noun.getCoveredText()+" "+verb.getCoveredText());
				}else if(verb.getType().equals("VBP")&&(noun.getType().equals("NN")||noun.getType().equals("NNP"))){
					if(debug) System.out.println("error found: "+noun.getCoveredText()+" "+verb.getCoveredText());
					errors++;
				}else if(verb.getType().equals("VBZ")&&(noun.getType().equals("NNS")||noun.getType().equals("NNPS"))){
					if(debug) System.out.println("error found: "+noun.getCoveredText()+" "+verb.getCoveredText());
					errors++;
				}else if(noun.getType().equals("PRP")){
					if(verb.getType().equals("VBZ")&&!isSingularPronoun(noun.getCoveredText())){
						if(debug) System.out.println("error found: "+noun.getCoveredText()+" "+verb.getCoveredText());
						errors++;
					}else if(verb.getType().equals("VBP")&&isSingularPronoun(noun.getCoveredText())){
						if(debug) System.out.println("error found: "+noun.getCoveredText()+" "+verb.getCoveredText());
						errors++;
					}
				}
			}
			covered.add(verb);
			covered.add(noun);
		}
		
		for(Parse child:p.getChildren()){
			//if(!isChild(child,np)&&!isChild(child,vp)){
				errors+=checkSentSV(child,debug,covered);
			//}
			
		}
		return errors;
	         
	}
	public static Parse getMainNoun(Parse p, ArrayList<Parse> covered){
		Parse noun=null;
		if(p.getChildren().length==0){
			return null;
		}
		for (Parse child : p.getChildren()){
			if(!covered.contains(child)&&child.getType().equals("PRP")||child.getType().equals("WDT")||child.getType().equals("WP")||child.getType().equals("NN")||child.getType().equals("NNS")||child.getType().equals("NNP")||child.getType().equals("NNPS")){
				noun=child;
			}
		}
		if(noun!=null){
			return noun;
		}else{
			for (Parse child : p.getChildren()){
				noun=getMainNoun(child,covered);
				if(noun!=null){
					return noun;
				}
			}
		}
		return null;
	}
	
	public static Parse getMainVerb(Parse p,ArrayList<Parse> covered){
		Parse noun=null;
		if(p.getChildren().length==0){
			return null;
		}
		for (Parse child : p.getChildren()){
			if(!covered.contains(child)&&child.getType().equals("VBZ")||child.getType().equals("VBP")){
				noun=child;
			}
		}
		if(noun!=null){
			return noun;
		}else{
			for (Parse child : p.getChildren()){
				noun=getMainVerb(child,covered);
				if(noun!=null){
					return noun;
				}
			}
		}
		return null;
	}
	public static Parse getNP(Parse p){
		Parse noun=null;
		if(p.getChildren().length==0){
			return null;
		}
		for (Parse child : p.getChildren()){
			if(child.getType().equals("NP")){
				noun=child;
			}
		}
		if(noun!=null){
			return noun;
		}else{
			for (Parse child : p.getChildren()){
				noun=getNP(child);
				if(noun!=null){
					return noun;
				}
			}
		}
		return null;
	}
	public static Parse getVP(Parse p){
		Parse noun=null;
		if(p.getChildren().length==0){
			return null;
		}
		for (Parse child : p.getChildren()){
			if(child.getType().equals("VP")){
				noun=child;
			}
		}
		if(noun!=null){
			return noun;
		}else{
			for (Parse child : p.getChildren()){
				noun=getVP(child);
				if(noun!=null){
					return noun;
				}
			}
		}
		return null;
	}
	public static boolean isChild(Parse p,Parse child){
		if(p==null||child==null||p.getChildren()==null){
			return false;
		}
			for (Parse kid : p.getChildren()){
				if(kid.compareTo(child)==0){
					return true;
				}
			}
			for (Parse kid : p.getChildren()){
				if(isChild(kid,child)){
					return true;
				}
			}
		
		return false;
	}
	public static boolean isSingularPronoun(String prn){
		return (prn.compareToIgnoreCase("it")==0||prn.compareToIgnoreCase("he")==0||prn.compareToIgnoreCase("she")==0);
	}
	/*public static int checkSV(Parse p){
		//find NP, VP combos
		int errors=0;
		System.out.println("showing main noun:");
		Parse noun=getMainNoun(p);
		if(noun!=null){
			noun.show();
		}else{
			System.out.println("not found");
		}
		System.out.println("showing main verb:");
		Parse verb=getMainVerb(p);
		if(verb!=null){
			verb.show();
		}else{
			System.out.println("not found");
		}
		
		if(verb!=null&&noun!=null){
			if(noun.getType().equals("WDT")||noun.getType().equals("WP")){
				System.out.println("no error found: "+noun.getCoveredText()+" "+verb.getCoveredText());
			}else if(verb.getType().equals("VBP")&&(noun.getType().equals("NN")||noun.getType().equals("NNP"))){
				System.out.println("error found: "+noun.getCoveredText()+" "+verb.getCoveredText());
				errors++;
			}else if(verb.getType().equals("VBZ")&&(noun.getType().equals("NNS")||noun.getType().equals("NNPS"))){
				System.out.println("error found: "+noun.getCoveredText()+" "+verb.getCoveredText());
				errors++;
			}
		}
		
		for(Parse child:p.getChildren()){
			errors+=checkSV(child);
		}
		return errors;
	         
	}*/
	/*public static void getNounPhrases(Parse p,ArrayList<String> list) {
	
    if (p.getType().equals("NP")) { //NP=noun phrase
         list.add(p.getCoveredText());
    }
    for (Parse child : p.getChildren())
         getNounPhrases(child,list);
}
public static void getVerbPhrases(Parse p,ArrayList<String> list) {
	
    if (p.getType().equals("VP")) { //NP=noun phrase
         list.add(p.getCoveredText());
    }
    for (Parse child : p.getChildren())
         getVerbPhrases(child,list);
}*/
}
