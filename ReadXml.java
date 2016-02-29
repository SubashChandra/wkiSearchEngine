import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class NodeComp implements Comparator<Node>
{
	@Override
	public int compare(Node n1, Node n2)
	{
		if(n1.value<n2.value)
			return 1;
		else
			return -1;
	}
}

public class ReadXml 
{
	static int indCount=0;
	static int fileCount=0;
	static long processTime;
	
	
	public void sortNdStemPostings(TreeMap<String,HashMap<Integer,Node>> mainIndex)
	{
		Node temp = new Node();
		TreeSet<Node> postings = new TreeSet<Node>(new NodeComp());
		
		try
		{
			PrintWriter writer = new PrintWriter("./rsc/index"+fileCount+".txt");
			for(String s:mainIndex.keySet()) {
				HashMap<Integer, Node> p = mainIndex.get(s);
				//iterate over the posting list
				for(Integer docid : p.keySet()) 
				{
					temp=p.get(docid);
					//assign weight to each document based on freq of tags and their priority
					temp.value = (temp.titleCount*70) + (temp.bodyCount>5?5*5:temp.bodyCount*5) + (temp.categoryCount>5?10*5:temp.categoryCount*10) + (temp.infoBoxCount>5?5*5:temp.infoBoxCount*5) + (temp.referencesCount>5?5*5:temp.referencesCount*5)+(temp.linksCount>5?5*5:temp.linksCount*5);
					//now create a list of all objects for a token and sort it and print the top 10 objects to a file 
					postings.add(temp);
				}	

				writer.print(s+":");
				int i=0;
				for(Node n:postings)
				{
					writer.print(n);
					i++;
					if(i==10)
						break;
				}
				writer.println();
				postings.clear();

			}
			writer.close();
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
		}

	}
	
	public void getData(String fname, HashMap<Integer,String> titleIndex, HashMap<String,Integer> stopWords)
	{
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			
			TreeMap<String,HashMap<Integer,Node>> mainIndex = new TreeMap<String,HashMap<Integer,Node>>();
			//now process each document
			Processor myObj = new Processor();
			
			

			
			DefaultHandler handler = new DefaultHandler() {

				boolean pageTag = false;
				boolean titleTag = false;
				boolean textTag = false;
				
				private String title;
				private String text;
				private StringBuilder sb = new StringBuilder(); 
				
				public String getTitle() {
					return title;
				}

				public void setTitle(String title) {
					this.title = title;
				}

				public String getText() {
					return text;
				}

				public void setText(String text) {
					this.text = text;
				}

				public void startElement(String uri, String localName,String qName, 
						Attributes attributes) throws SAXException {

					if (qName.equalsIgnoreCase("page")) {
						pageTag = true;
					}

					if (qName.equalsIgnoreCase("title")) {
						if(pageTag)
						{
							titleTag = true;
							//sb=new StringBuilder();
							sb.setLength(0);
							sb.trimToSize();
						}
					}

					if (qName.equalsIgnoreCase("text")) {
						if(pageTag)
						{
							textTag = true;
							sb=new StringBuilder();
						}
					}
				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					
					if(qName.equalsIgnoreCase("title"))
					{
						this.setTitle(sb.toString().toLowerCase());
						//System.out.println("*********************"+this.getTitle());
						titleTag = false;
						
					}
					
					if(qName.equalsIgnoreCase("text"))
					{
						this.setText(sb.toString().toLowerCase());
						//System.out.println("###################"+this.getText());
						titleIndex.put(indCount, this.getTitle());
						//System.out.println(this.getTitle());
						long start = System.currentTimeMillis();
						myObj.process(indCount,this.getTitle(),this.getText(), mainIndex,titleIndex,stopWords);
						processTime += System.currentTimeMillis() - start;
						//docIndex.put(indCount, this.getText());
						indCount++;
						if(indCount%1000==0)
						{
							sortNdStemPostings(mainIndex);
							
							fileCount++;
							mainIndex.clear();
							System.out.println(indCount);
							System.out.println(processTime);
						}
						textTag = false;
						pageTag = false;
					}

				}

				public void characters(char ch[], int start, int length) throws SAXException {

					if (titleTag) {
						sb.append(new String(ch, start, length));
					}

					if (textTag) {
						sb.append(new String(ch, start, length));
					}
				}

			};

			saxParser.parse(fname, handler);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
