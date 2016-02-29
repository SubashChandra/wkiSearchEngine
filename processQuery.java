import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;




public class processQuery {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String query;
		Scanner reader = new Scanner(System.in);
		
		BufferedReader fileReader;
		RandomAccessFile rafReader;
		String line;
		
		
		HashMap<Integer,Integer> titlesSec = new HashMap<Integer,Integer>();
		try
		{
			int counter=0;
			fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("./../rsc/titlesSec.txt")));
			try 
			{
				while((line = fileReader.readLine()) != null)
				{
					titlesSec.put(counter, Integer.parseInt(line));
					counter++;
				}
				fileReader.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Stemmer s ;
		TreeMap<MyRanker,MyRanker> mr = new TreeMap<MyRanker, MyRanker>(new Comparator<MyRanker>(){

			@Override
			public int compare(MyRanker o1, MyRanker o2) {
				if(o1.getVal() < o2.getVal()){
					return 1;
				}
				if(o1.getVal() > o2.getVal()){
					return -1;
				}
				if(o1.getId() < o2.getId()){
					return 1;
				}
				if(o1.getId() > o2.getId()){
					return -1;
				}
				return -1;
			}
			
		});
		System.out.println("enter a query or 'exit' to quit");
		query = reader.nextLine().toLowerCase();
		while(query.compareTo("exit")!=0)
		{
			//System.out.println(query);
			if(!query.contains(":"))
			{
				
				String [] qWords = query.split(" ");
				
				//System.out.println(qWords.length);
//				ValueComparator bvc = new ValueComparator(docList);
//				TreeMap<Integer,Double> docList = new TreeMap<Integer,Double>(bvc);
//				TreeMap<Integer,Double> docsTreeMap = new TreeMap<Integer,Double>(bvc);
				
				for (String str:qWords)
				{
					//System.out.println(str);
					s = new Stemmer();
					s.add(str.toCharArray(), str.length());
					s.stem();

					str = s.toString();
					//System.out.println(str);
					//now for each word in qWords, open respective secIndex
					Integer pOffset=0;
					try
					{
						//System.out.println(str.charAt(0));
						fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("./../rsc/secondary"+str.charAt(0)+".txt")));
						try 
						{
							
							while((line = fileReader.readLine()) != null)
							{
								//iterate over the file and get the offset for the word
								String[] temp = line.split(" ");
								//len = temp.codePointCount(0, temp.length());
								if(temp[0].compareTo(str)==0)
								{
									pOffset=Integer.parseInt(temp[1]);
									//System.out.println(pOffset);
									fileReader.close();
									break;
								}
							}
							
							//now open the corresponding primary index file 
							//and get the postingList for the string 
							rafReader = new RandomAccessFile("./../rsc/merged"+str.charAt(0)+".txt","rw");
							//System.out.println(str.charAt(0));
							
							try
							{
								//System.out.println(pOffset);
								rafReader.seek(pOffset);
								String curLine = rafReader.readLine();
								//System.out.println(curLine);
								String postings = curLine.split("-")[1];
								//System.out.println(postings);
								String[] postingsList = postings.split("\\|");
								double curIdf=Math.log10(16244931/postingsList.length); //save the idf value
								
								int docId;
								String posting;
								String[] tags;
								String [] counts;
								int val;
								double tf;
								for(String st:postingsList)
								{
									
									docId = Integer.parseInt(st.split(" ")[0]);
									posting = st.split(" ")[1];
									//System.out.println(docId+" "+posting);
									//posting="b20t1c10";
									
									Map<String, Integer> vals = new HashMap<String, Integer>();
									String key = "";
									String value = "";
									
									//process the postings and create a map with tags as keys and values
									for(int j = 0; j < posting.length(); j++)
									{
										if(posting.charAt(j) == 'b' || 
												posting.charAt(j) == 'i' ||
												posting.charAt(j) == 't' ||
												posting.charAt(j) == 'c' ||
												posting.charAt(j) == 'l' || 
												posting.charAt(j) == 'r'){
											if(!key.equals("")){
												vals.put(key, Integer.parseInt(value));
												//System.out.println(key+" = "+value);
											}
											key = posting.charAt(j)+"";
											value = "";
										} else {
											value += posting.charAt(j)+"";
										}
									}
									
									
									if(!key.equals(""))
										vals.put(key, Integer.parseInt(value));
									
									
									//iterate over the hashmap and calculate value
									val=0; //value after computing freq*weight for each tag
									tf=0; //log(1+val)
									//System.out.println(vals.size());
									for(String k : vals.keySet())
									{
										//System.out.println(k+" "+vals.get(k));
										if(k.equals("t"))
										{
											val+=vals.get(k)*1000;
										}
										else if(k.equals("i"))
										{
											val+=vals.get(k)*200;
										}
										else if(k.equals("c"))
										{
											val+=vals.get(k)*200;
										}
										else if(k.equals("l"))
										{
											val+=vals.get(k)*150;
										}
										else if(k.equals("r"))
										{
											val+=vals.get(k)*150;
										}
										else if(k.equals("b"))
										{
											val+=vals.get(k)*100;
										}
									}
									tf=1+Math.log10(val);
									//System.out.println(val);
									//System.out.println(tf);
									MyRanker tmp = new MyRanker();
									tmp.setId(docId);									
									if(mr.containsKey(tmp))
									{
										MyRanker oldVal = mr.get(tmp);
										oldVal.setVal(oldVal.getVal()+tf);
										//oldVal+=tf;
									}
									else
									{
										MyRanker oldVal = new MyRanker();
										oldVal.setId(docId);
										oldVal.setVal(tf);
										mr.put(oldVal, oldVal);
									}
								}
								rafReader.close();
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						fileReader.close();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//insert all the hashmap entries to treemap
					//now sort the treemap treedoclist via values, highest first
					
					
//					docsTreeMap.putAll(docList);
					
					
					
				} 
				
				
				
				//print output here
				System.out.println("***Top Results***\n\n");
				
				//now for each docId in the docsTreeMap, get the offset and get the book title
				
				int secLine;
				int primaryOffset; //offset in primary index
				int primaryLine; //the line in the 1000 loaded titles
				int top10Counter=0;
				
				List<Integer> li = new ArrayList<Integer>();
				int i = 0;
				for(MyRanker d1 : mr.keySet()){
					if(!li.contains(d1.getId())){
						
						li.add(d1.getId());
						i++;
					}
					if( i== 10)
						break;
				}
				
				int countId=1;
				for(Integer d: li)
				{
//					int d = d1.getId();
					//System.out.println(">"+d);
					secLine=d/1000; //will give me line where i can start searching from sec file
					primaryOffset=titlesSec.get(secLine);
					primaryLine=d%1000;
					//now starting at the offset, read the next thousand line
					try
					{
						rafReader = new RandomAccessFile("./../rsc/titles.txt","rw");
						try
						{
							rafReader.seek(primaryOffset);
							int counter=0;
							
							//read the line number primaryLine
							while((line = rafReader.readLine()) != null && counter<primaryLine)
							{
								//System.out.println(line);
								counter++;
							}
							
							//now line will hav the title for that docId
							//System.out.println(counter);
							System.out.println(countId+ " -> " +line);
						}	

						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						rafReader.close();
						top10Counter++;
						if(top10Counter==10)
							break;
						countId++;
					}
					
					
					
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				System.out.println("\n");
//				docList.clear();
//				docsTreeMap.clear();
				mr.clear();
			} //end of if
		//*************************************************************************************
			else
			{
				
				String [] qWords = query.split(";");
				
				//System.out.println(qWords.length);
//				ValueComparator bvc = new ValueComparator(docList);
//				TreeMap<Integer,Double> docList = new TreeMap<Integer,Double>(bvc);
//				TreeMap<Integer,Double> docsTreeMap = new TreeMap<Integer,Double>(bvc);
				
				int tflag=0;
				int iflag=0;
				int cflag=0;
				int lflag=0;
				int rflag=0;
				int bflag=0;

				for (String str:qWords)
				{
					//System.out.println(str);
					
					if(str.charAt(0)=='t')
					{
						tflag=1;
					}
					else if(str.charAt(0)=='i')
					{
						iflag=1;
					}
					else if(str.charAt(0)=='c')
					{
						cflag=1;
					}
					else if(str.charAt(0)=='l')
					{
						lflag=1;
					}
					else if(str.charAt(0)=='r')
					{
						rflag=1;
					}
					else if(str.charAt(0)=='b')
					{
						bflag=1;
					}
					//System.out.println(str);
					str=str.split(":")[1];
					
					//System.out.println("->"+str);
					s = new Stemmer();
					s.add(str.toCharArray(), str.length());
					s.stem();

					str = s.toString();
					//System.out.println(str);
					//now for each word in qWords, open respective secIndex
					Integer pOffset=0;
					try
					{
						//System.out.println(str.charAt(0));
						fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("./../rsc/secondary"+str.charAt(0)+".txt")));
						try 
						{
							
							while((line = fileReader.readLine()) != null)
							{
								//iterate over the file and get the offset for the word
								String[] temp = line.split(" ");
								//len = temp.codePointCount(0, temp.length());
								if(temp[0].compareTo(str)==0)
								{
									pOffset=Integer.parseInt(temp[1]);
									//System.out.println(pOffset);
									fileReader.close();
									break;
								}
							}
							
							//now open the corresponding primary index file 
							//and get the postingList for the string 
							rafReader = new RandomAccessFile("./../rsc/merged"+str.charAt(0)+".txt","rw");
							//System.out.println(str.charAt(0));
							
							try
							{
								//System.out.println(pOffset);
								rafReader.seek(pOffset);
								String curLine = rafReader.readLine();
								//System.out.println(curLine);
								String postings = curLine.split("-")[1];
								//System.out.println(postings);
								String[] postingsList = postings.split("\\|");
								double curIdf=Math.log10(16244931/postingsList.length); //save the idf value
								
								int docId;
								String posting;
								String[] tags;
								String [] counts;
								int val;
								double tf;
								for(String st:postingsList)
								{
									
									docId = Integer.parseInt(st.split(" ")[0]);
									posting = st.split(" ")[1];
									//System.out.println(docId+" "+posting);
									//posting="b20t1c10";
									
									Map<String, Integer> vals = new HashMap<String, Integer>();
									String key = "";
									String value = "";
									
									//process the postings and create a map with tags as keys and values
									for(int j = 0; j < posting.length(); j++)
									{
										if(posting.charAt(j) == 'b' || 
												posting.charAt(j) == 'i' ||
												posting.charAt(j) == 't' ||
												posting.charAt(j) == 'c' ||
												posting.charAt(j) == 'l' || 
												posting.charAt(j) == 'r'){
											if(!key.equals("")){
												vals.put(key, Integer.parseInt(value));
												//System.out.println(key+" = "+value);
											}
											key = posting.charAt(j)+"";
											value = "";
										} else {
											value += posting.charAt(j)+"";
										}
									}
									
									
									if(!key.equals(""))
										vals.put(key, Integer.parseInt(value));
									
									
									//iterate over the hashmap and calculate value
									val=0; //value after computing freq*weight for each tag
									tf=0; //log(1+val)
									//System.out.println(vals.size());
									for(String k : vals.keySet())
									{
										//System.out.println(k+" "+vals.get(k));
										if(k.equals("t"))
										{
											int tweight=1000;
											if(tflag==1)
												tweight+=200;
											val+=vals.get(k)*tweight;
											
										}
										else if(k.equals("i"))
										{
											int iweight=200;
											if(iflag==1)
												iweight+=200;
											val+=vals.get(k)*iweight;
										}
										else if(k.equals("c"))
										{
											int cweight=200;
											if(cflag==1)
												cweight+=200;
											val+=vals.get(k)*cweight;
										}
										else if(k.equals("l"))
										{
											int lweight=150;
											if(lflag==1)
												lweight+=200;
											val+=vals.get(k)*lweight;
										}
										else if(k.equals("r"))
										{
											int rweight=150;
											if(rflag==1)
												rweight+=200;
											val+=vals.get(k)*rweight;
										}
										else if(k.equals("b"))
										{
											int bweight=100;
											if(bflag==1)
												bweight+=200;
											val+=vals.get(k)*bweight;
										}
										
										
									
									}
									tf=1+Math.log10(val);
									//System.out.println(val);
									//System.out.println(tf);
									MyRanker tmp = new MyRanker();
									tmp.setId(docId);									
									if(mr.containsKey(tmp))
									{
										MyRanker oldVal = mr.get(tmp);
										oldVal.setVal(oldVal.getVal()+tf);
										//oldVal+=tf;
									}
									else
									{
										MyRanker oldVal = new MyRanker();
										oldVal.setId(docId);
										oldVal.setVal(tf);
										mr.put(oldVal, oldVal);
									}
								}
								rafReader.close();
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						fileReader.close();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//insert all the hashmap entries to treemap
					//now sort the treemap treedoclist via values, highest first
					
					
//					docsTreeMap.putAll(docList);
					
					
					
				} //end of if
				
				
				
				//print output here
				System.out.println("***Top Results***\n\n");
				
				//now for each docId in the docsTreeMap, get the offset and get the book title
				
				int secLine;
				int primaryOffset; //offset in primary index
				int primaryLine; //the line in the 1000 loaded titles
				int top10Counter=0;
				
				List<Integer> li = new ArrayList<Integer>();
				int i = 0;
				for(MyRanker d1 : mr.keySet()){
					if(!li.contains(d1.getId())){
						
						li.add(d1.getId());
						i++;
					}
					if( i== 10)
						break;
				}
				
				int countId=1;
				for(Integer d: li)
				{
//					int d = d1.getId();
					//System.out.println(">"+d);
					secLine=d/1000; //will give me line where i can start searching from sec file
					primaryOffset=titlesSec.get(secLine);
					primaryLine=d%1000;
					//now starting at the offset, read the next thousand line
					try
					{
						rafReader = new RandomAccessFile("./../rsc/titles.txt","rw");
						try
						{
							rafReader.seek(primaryOffset);
							int counter=0;
							
							//read the line number primaryLine
							while((line = rafReader.readLine()) != null && counter<primaryLine)
							{
								//System.out.println(line);
								counter++;
							}
							
							//now line will hav the title for that docId
							//System.out.println(counter);
							System.out.println(countId+ " -> " +line);
						}	

						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						rafReader.close();
						top10Counter++;
						if(top10Counter==10)
							break;
						countId++;
					}
					
					
					
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				System.out.println("\n");
//				docList.clear();
//				docsTreeMap.clear();
				mr.clear();
			}
			System.out.println("enter a query or 'exit' to quit");
			query = reader.nextLine();
		}
	
		
	}
/*	static class ValueComparator implements Comparator {
		Map base;
		public ValueComparator(Map base) {
			this.base = base;
		}
		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(Object a, Object b) {
			if ((Double)base.get(a) >= (Double)base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}*/
	

}
