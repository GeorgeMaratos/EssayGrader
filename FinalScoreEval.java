package cs421nlp;

public class FinalScoreEval {
	public static int getScore(int a1, int b1, int c1, int d1, int a2, int b2, int c){
		return a1+b1+c1+2*d1+2*a2+3*b2+2*c;
		
	}
	public static String getRank(int a1, int b1, int c1, int d1, int a2, int b2, int c){
		int score=getScore(a1,b1,c1,d1,a2,b2,c);
		if(score>44){
			return "HIGH";
		}else if(score>36){
			return "MEDIUM";
			
		}else{
			return "LOW";
		}
	}
}
