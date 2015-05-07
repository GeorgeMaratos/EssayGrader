package cs421nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

//import org.apache.lucene.search.spell.PlainTextDictionary;
//import org.apache.lucene.search.spell.SpellChecker;
//import org.apache.lucene.search.suggest.FileDictionary;








public class Main {
	private static SentenceDetectorME sentenceDetector;
	private static Parser parser=null;
	public static void main(String[] args) throws IOException { //path to wordnet dict
		//!!!! initialize
		//System.out.println("gets here in main");
		/*POSModel model = new POSModelLoader()	.load(new File("en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);*/
		InputStream is = new FileInputStream("src/spellchecker/en-parser-chunking.bin");
		 
		ParserModel model = new ParserModel(is);
	 
		parser = ParserFactory.create(model);
		is.close();
		OutputFormatter.init(); 
		
		/*InputStream modelIn = new FileInputStream("src/spellchecker/en-sent.bin");
		SentenceModel senmodel= new SentenceModel(modelIn);
		sentenceDetector= new SentenceDetectorME(senmodel);*/
		SentenceSplitter.init(sentenceDetector);
		
		SpellingChecker checker = new SpellingChecker();
		SVEval.init(parser);
		PronounEval.init(parser);
		TopicEval.init();
		WellFormedness wf=new WellFormedness(parser);
		
		//!!!! loads whole folder of essays for debug purposes,comment out the one you want
		
		//String[] essays=loadDirectoryToString("P5-original/high");
		//String[] essays=loadDirectoryToString("P5-original/low");
		//String[] essays=loadDirectoryToString("P5-original/medium");
		String[] essays=loadDirectoryToString("input/test/original");
		String[] filenames=loadDirectoryFN("input/test/original");
		//double spavg=0,svavg=0,vavg=0;
		int sprank=0,svrank=0, vrank=0,lrank=0, wrank=0, crank=0, trank=0;
		for(int i=0;i<essays.length;i++){
			System.out.println("working on: "+filenames[i]);
			vrank=Verbeval.evalEssay(essays[i], parser, false);
			System.out.println("verb tense rank: "+vrank);
			sprank=SpellingChecker.eval(essays[i],false);
			System.out.println("spelling rank: "+sprank);
			svrank=SVEval.eval(essays[i],false);
			System.out.println("subject verb rank: "+svrank);
			lrank=SentenceSplitter.eval(essays[i]);
			System.out.println("length of essay rank: "+lrank);
			wrank=wf.feedEssay(essays[i], false);
			System.out.println("wellformedness rank: "+wrank);
			crank=PronounEval.eval(essays[i], false);
			System.out.println("coherence rank: "+crank);
			
			trank=TopicEval.eval(essays[i], false);
			System.out.println("topic rank: "+trank);
			OutputFormatter.addScore(filenames[i], sprank, svrank, vrank, wrank, crank, trank, lrank, FinalScoreEval.getScore(vrank, sprank, svrank, wrank, crank, trank, lrank), FinalScoreEval.getRank(vrank, sprank, svrank, wrank, crank, trank, lrank));
			//System.out.println("spelling rank "+sprank+ " verb rank "+ vrank+" sv rank "+svrank+ " num sentences "+lrank);
			System.out.println("Score is "+FinalScoreEval.getScore(vrank, sprank, svrank, wrank, crank, trank, lrank));
		}
		//System.out.println("total errors "+sum/10.);
		OutputFormatter.writeFile();
		System.out.println("done wrote "+essays.length+" scores");
		
		
		//!!! comment out this section to test one essay
		
		

		
		
		
		
		/*String essay2;
		essay2="John likes to go to the store. When he goes to the store it's fun. They don't go often. He doesn't go either. Their dog went away.";
		//essay2=loadFileToString("P5-original/high/11580.txt");
		//essay2=loadFileToString("P5-original/high/11717.txt");
		//essay2=loadFileToString("P5-original/high/23062.txt");
		//essay2=loadFileToString("P5-original/low/106467.txt");
		//essay2=loadFileToString("P5-original/low/107740.txt");
		//essay1=loadFileToString("P5-original/low/129378.txt");
		//essay1=loadFileToString("P5-original/low/131324.txt");
		//essay1=loadFileToString("P5-original/high/14397.txt");
		//essay1=loadFileToString("P5-original/high/32672.txt");
		//essay2=loadFileToString("P5-original/high/22698.txt");
		//essay2=loadFileToString("P5-original/medium/23897.txt");
		//essay1=loadFileToString("P5-original/medium/10215.txt");
		//essay2=loadFileToString("P5-original/medium/22597.txt");
		//essay1= "Firt of all, the group led will be not agree together each one want be the led.";
		//double rank=SVeval.evalEssay(essay2, tagger, true);
		//double errors=SVEval.eval(essay2,true);
		int errors=PronounEval.eval(essay2, true);
		System.out.println("errors ="+errors);
		/*String essay2;
		essay2=loadFileToString("P5-original/high/11580.txt");
		String[] sentences=SentenceSplitter.split(essay2);
		for(String sent:sentences){
			System.out.println(sent);
		}*/
		
		//String essay="I am jumping around. We jump around. We can jump tommorow. He was going around. He was taken yesterday.";
		/*String essay="I jumping right now. I jump ran around. We were jump around. We taken jumping tommorow. He was is around.";
		
		

		double errors=Verbeval.evalEssay(essay, parser, true);
		System.out.println("errors: "+errors);*/
		
	}
		
	public static String loadFileToString(String filename) throws IOException{
		FileReader essayfile = new FileReader(filename);
		BufferedReader reader= new BufferedReader(essayfile);
		String aLine;
		String essay=" ";
		while ( ( aLine = reader.readLine( ) ) != null ) {
			essay=essay+aLine;
		}
		reader.close();
		return essay;
	    
	}
	public static String[] loadDirectoryToString(String dirname) throws IOException{
		
		File dir=new File(dirname);
		File[] listOfFiles = dir.listFiles();
		ArrayList<String> files=new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        files.add(listOfFiles[i].getName());
	      }
	    }
		String[] essays=new String[files.size()];
		for(int i=0;i<files.size();i++){
			essays[i]=loadFileToString(dirname+"/"+files.get(i));
		}
		return essays;
	    
	}
public static String[] loadDirectoryFN(String dirname) throws IOException{
		
		File dir=new File(dirname);
		File[] listOfFiles = dir.listFiles();
		ArrayList<String> files=new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        files.add(listOfFiles[i].getName());
	      }
	    }
		String[] essays=new String[files.size()];
		for(int i=0;i<files.size();i++){
			essays[i]=files.get(i);
		}
		return essays;
	    
	}
	
		
	
}

