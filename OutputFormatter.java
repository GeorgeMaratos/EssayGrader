package cs421nlp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

	
public class OutputFormatter {
	private static Vector<String> output;
	public static void init(){
		 output = new Vector<String>();
	}
	
	public static void addScore(String essayName, int onea, int  oneb, int onec, int oned, int twoa,
			int twob, int threea, int finalScore, String grade) {
		
		String toAdd = essayName + '\t' + onea + '\t' + oneb + '\t' + onec + '\t'+ oned + '\t' +
				twoa + '\t' + twob + '\t' + threea + '\t' + finalScore + '\t' + grade;
		
		output.add(toAdd);
	}
	
	public static void writeFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("output/result.txt", "UTF-8");
		
		for(int i=0; i< output.size();i++)
			writer.println(output.elementAt(i));
		
		writer.close();
	}

}