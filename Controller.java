import java.io.*;
import java.util.*;

public class Controller {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		//controll passes from here to all other modules
		// 1-> to ReadXml and back
		// 2-> to Tokenizer and back
		// 3-> to StopWords and back
		// 4-> to Stemmer and back
		// 5-> to Indexer and back
		
		long startTime=System.currentTimeMillis();
		//titleIndex has id to page title
		HashMap<Integer,String> titleIndex = new HashMap<Integer,String>();
		
		String [] words = {"ref","redirect","a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
		HashMap<String,Integer> stopWords = new HashMap<String,Integer>();
		for (String str: words)
		{
			stopWords.put(str, 1);
		}
		
		
		ReadXml reader = new ReadXml();
		reader.getData("./rsc/wiki300.xml", titleIndex,stopWords);
		
		/*try
		{
			PrintWriter writer = new PrintWriter("tempDump1.txt");
			for(Integer id: docIndex.keySet())
			{
				writer.println("\n******** ID: "+id);
				writer.println("\n##############");
				writer.println(docIndex.get(id));
			}
			writer.close();
			
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
		}*/
		
				
		
		
		/*for(String s:mainIndex.keySet()) {
			HashMap<Integer, Node> p = mainIndex.get(s);
			System.out.print(s+":");
			for(Integer docid : p.keySet()) {
				System.out.print(docid + "-" + p.get(docid));
			}
			System.out.println();
		}*/
		
			
		/*try
		{
			PrintWriter writer = new PrintWriter("index.txt");
			for(String s:mainIndex.keySet()) {
				HashMap<Integer, Node> p = mainIndex.get(s);
				writer.print(s+":");
				for(Integer docid : p.keySet()) {
					writer.print(docid + "-" + p.get(docid));
				}
				writer.println();
			}
			writer.close();
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
		}*/
		/*try
		{
			PrintWriter writer = new PrintWriter("tempDump2.txt");
			for(Integer id: docIndex.keySet())
			{
				writer.println("\n******** ID: "+id);
				writer.println("\n##############");
				writer.println(docIndex.get(id));
			}
			writer.close();
			
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
		}*/
		long endTime=System.currentTimeMillis();
		System.out.println((endTime-startTime)/1000.00);
	}

}
