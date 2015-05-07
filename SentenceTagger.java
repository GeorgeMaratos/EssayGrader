package cs421nlp;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class SentenceTagger {
	public static String[] getTags(String sentence, Parser parser){
		if(sentence.trim().length()<3){
			return new String[]{""};
		}
		Parse[] parses=ParserTool.parseLine(sentence, parser, 1);
		if(parses.length<=0){
			return null;
		}
		Parse p=parses[0];
		
		String[] tags=new String[p.getTagNodes().length];
		for(int i=0;i<p.getTagNodes().length;i++){
			tags[i]=p.getTagNodes()[i].getType();
		}
		return  tags;
	}
	public static String[] getTokens(String sentence){
		
		return WhitespaceTokenizer.INSTANCE.tokenize(sentence);
	}
	public static Parse[] getParseNodes(String sentence,Parser parser){
		if(sentence.trim().length()<3){
			return new Parse[0];
		}
		Parse[] parses=ParserTool.parseLine(sentence, parser, 1);
		if(parses.length<=0){
			return null;
		}
		Parse p=parses[0];
		return p.getTagNodes();
		
	}
}
