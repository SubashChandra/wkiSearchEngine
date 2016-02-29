import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processor 
{

	public void stemNdIndex(TreeMap<String, HashMap<Integer, Node>> mainIndex, String[] str,char tag,
			Integer pageId,HashMap<String,Integer> stopWords) 
	{
		int i;
		String temp, newStr;
		Stemmer s ;
		for (i=0;i<str.length;i++) 
		{
			temp=str[i];
			if(temp==null)
			{
				continue;
			}
			
			s = new Stemmer();
			//System.out.println("before:"+temp);				
			temp=temp.replaceAll("[^a-zA-Z0-9 ]"," ").trim();
			//if stop word, return 
			Integer val= stopWords.get(temp);
			if(val!=null)
			{
				continue;
			}
			
			
			s.add(temp.toCharArray(), temp.length());
			s.stem();

			newStr = s.toString();
			
			
			
			
			
			if(newStr.length()>2)
			{
				//System.out.println("after:"+newStr);

				if(mainIndex.containsKey(newStr))
				{
					HashMap<Integer, Node> obj = mainIndex.get(newStr);

					//if object for the pageId already exists
					if(obj.containsKey(pageId))
					{
						Node page = obj.get(pageId);
						switch(tag)
						{
						case 'C':
							page.categoryCount++;
							break;
						case 'I':
							page.infoBoxCount++;
							break;
						case 'T':
							page.titleCount++;
							break;
						case 'L':
							page.linksCount++;
							break;
						case 'R':
							page.referencesCount++;
							break;
						case 'B':
							page.bodyCount++;
							break;

						}
						
					} 
					//if object for the page doesn;t exist
					else {
						Node page = new Node();
						page.docId=pageId;
						
						switch(tag)
						{
						case 'C':
							page.categoryCount++;
							break;
						case 'I':
							page.infoBoxCount++;
							break;
						case 'T':
							page.titleCount++;
							break;
						case 'L':
							page.linksCount++;
							break;
						case 'R':
							page.referencesCount++;
							break;
						case 'B':
							page.bodyCount++;
							break;
						}
						obj.put(pageId, page);
					}
				}

				//if string "newStr" doesn't exist in the index 
				else
				{
					HashMap<Integer, Node> obj = new HashMap<Integer, Node>();
					Node page = new Node();
					page.docId = pageId;
					switch(tag)
					{
					case 'C':
						page.categoryCount++;
						break;
					case 'I':
						page.infoBoxCount++;
						break;
					case 'T':
						page.titleCount++;
						break;
					case 'L':
						page.linksCount++;
						break;
					case 'R':
						page.referencesCount++;
						break;
					case 'B':
						page.bodyCount++;
						break;

					}
					obj.put(pageId, page);
					mainIndex.put(newStr, obj);
				}
			}
		}

	}
	String catRegex = "\\[\\[category:(.*?)\\]\\]";
	Pattern catPattern = Pattern.compile(catRegex);
	String linkRegex ="==external links==(.*?)\n\n";
	Pattern linkPattern = Pattern.compile(linkRegex,Pattern.DOTALL);
	String refRegex = "==references==(.*?)\n\n";
	Pattern refPattern = Pattern.compile(refRegex,Pattern.DOTALL);
	String infoRegex= "\\{\\{infobox.*?\\}\\}\n";
	Pattern infoPattern = Pattern.compile(infoRegex,Pattern.DOTALL);
	String bodyRegex = "\\{\\{.*?\\}\\}";
	Pattern bodyPattern = Pattern.compile(bodyRegex,Pattern.DOTALL);

	String linkInternal ="\\* \\[.*?\\s(.*?)\\]";
	Pattern lInternal = Pattern.compile(linkInternal);
	String refInternal ="\\*\\{\\{cite.*?title=(.*?):.*?\\}\\}";
	Pattern rInternal = Pattern.compile(refInternal);
	String infoInternal = ".*?=(.*?)\n";
	Pattern iInternal = Pattern.compile(infoInternal);
	String urlInternal = "\\*[ ]{0,1}\\[http:.*?\\]";
	Pattern uInternal = Pattern.compile(urlInternal);


	public void process(int docId, String title,String content, TreeMap<String, HashMap<Integer,Node>> mainIndex,HashMap<Integer,String> titleIndex, HashMap<String, Integer> stopWords) 
	{
		
		//handle title
		String[] titles= new String[1];
		titles[0]=title;
		stemNdIndex(mainIndex, titles, 'T', docId,stopWords);
		
		
		
		//handle categories
		Matcher m = catPattern.matcher(content);
		while (m.find()) {
			//System.out.println(">>>"+m.group(1).replaceAll("[^a-zA-Z0-9]", ""));
			stemNdIndex(mainIndex, m.group(1).replaceAll("[^a-zA-Z0-9]", " ").split(" "), 'C', docId,stopWords);
		}
		content = m.replaceAll("");


		//handles links
		Matcher m1 = linkPattern.matcher(content);
		if(m1.find()) {
			//System.out.println(m1.group(1));
			//now match internally to get the text
			Matcher m2 = lInternal.matcher(m1.group(1));
			while(m2.find())
			{
				//System.out.println(m2.group(1).replaceAll("\\."," ")+"###");
				stemNdIndex(mainIndex,m2.group(1).replaceAll("\\."," ").split(" "),'L',docId,stopWords);
			}
		}
		content = m1.replaceAll("");

		//handle references
		Matcher m3 = refPattern.matcher(content);
		if(m3.find())
		{
			//System.out.println(m3.group(1));
			Matcher m4 = rInternal.matcher(m3.group(1));
			while(m4.find())
			{
				//System.out.println(m4.group(1));
				stemNdIndex(mainIndex, m4.group(1).split(" "), 'R', docId,stopWords);
			}	
		}
		content = m3.replaceAll("");

		//handle info box
		Matcher m5 = infoPattern.matcher(content);
		if(m5.find())
		{
			//System.out.println(m5.group(0));
			Matcher m6 = iInternal.matcher(m5.group(0));
			while(m6.find())
			{
				//System.out.println(m6.group(1).replaceAll("[^a-zA-Z0-9]", " "));
				stemNdIndex(mainIndex, m6.group(1).replaceAll("[^a-zA-Z0-9]", " ").split(" "), 'I', docId,stopWords);
			}	
		}
		content = m5.replaceAll("");

		//handle body
		Matcher m7 = bodyPattern.matcher(content);
		content = m7.replaceAll(" ");
		Matcher m8 = uInternal.matcher(content);
		content = m8.replaceAll(" ");


		//System.out.println(docIndex.get(2).replaceAll("[^a-zA-Z0-9]", " "));
		stemNdIndex(mainIndex,content.replaceAll("[^a-zA-Z0-9]", " ").split(" "),'B',docId,stopWords);
		
	}
}