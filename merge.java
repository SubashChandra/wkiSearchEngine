import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class merge
{
	private List<BufferedReader> readers;
	//track if each file is still not empty
	private ArrayList<Integer> isEmpty = new ArrayList<Integer>();
	//keep track of total no of non-empty files
	private int activeFiles;
	
	private int totalFiles;
	public merge(int files)
	{
		readers = new ArrayList<BufferedReader>();
		totalFiles=files;
		activeFiles=files;
		
	}
	
	void initFiles(){
		
		try{
			int i = 0;
			while(i < totalFiles ){
				readers.add(new BufferedReader(new InputStreamReader(new FileInputStream("rsc/index"+i+".txt"))));
				isEmpty.add(1); //1 indicates not empty
				i++;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	String getLine(BufferedReader br, int fileId)
	{
		String tmp = null;
		try {
			if((tmp = br.readLine()) == null){
				br.close();
				//mark end
				isEmpty.set(fileId, 0); //mark that the file has reached EOF
				activeFiles--; //decrement the no of active files
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmp;
	}
	
	public static void main(String[] args)
	{
		merge ob= new merge(12);
		ob.initFiles();
		
		int i;
		
		
		
		//contains all the top strings in each file
		TreeSet<String> currentStrings = new TreeSet<String>();
		//contains string and list of files where each of current string is
		HashMap<String,ArrayList <Integer>> filesList = new HashMap<String,ArrayList<Integer>>();  
		//postings for the top string from each file
		ArrayList<String> filePostings = new ArrayList<String>();
		
		//read the first line in each file
		String curLine;
		for(i=0;i<ob.totalFiles;i++)
		{
			//System.out.println(i+"****");
			curLine = ob.getLine(ob.readers.get(i),i);
			//System.out.println(i+"  "+curLine);
			String[] slist = curLine.split(":");
			//System.out.println(slist[0]+ " "+slist[1]);
			
			
			//add current string to treeset
			currentStrings.add(slist[0]);
			
			//check if there is entry for the current string
			//if entry is already there, add current fileId to its list
			
			ArrayList<Integer> fList; //temp var
			if(filesList.containsKey(slist[0]))
			{
				fList = filesList.get(slist[0]);
				fList.add(i);
				
			}
			
			//if not create a new entry for the string
			else
			{
				fList = new ArrayList<Integer>();
				fList.add(i);
				filesList.put(slist[0], fList);
			}
			//update hashmap
			
			
			filePostings.add(slist[1]);
			
		}
		
		int firstFile = 1;
		
		//now do a comparasion and run
		String minString;
		
		ArrayList<Integer> fList;
		PrintWriter writer=null;
		char prevChar='0';
		while(ob.activeFiles>0)
		{
			//get the smallest string and remove it from treeset
			
			minString=currentStrings.first();
			//System.out.println(minString);
			currentStrings.remove(minString);
			//System.out.println(minString);
			
			 
			
			
			if(firstFile==1)
			{
				try
				{
					prevChar=minString.charAt(0);
					writer = new PrintWriter("./rsc/mergeIndex-"+prevChar+".txt");
					firstFile=0;
				}
				catch (IOException e)
				{
					System.out.println(e.toString());
				}
				//System.out.println(minString.charAt(0));
				
			}
			else if(prevChar != minString.charAt(0))
			{
				writer.close();
				prevChar=minString.charAt(0);
				try
				{
					writer = new PrintWriter("./rsc/mergeIndex-"+prevChar+".txt");
				}
				catch (IOException e)
				{
					System.out.println(e.toString());
				}
			}

			//get the file list which has the current string
			//System.out.println(minString);
			//System.out.println(filesList);
			fList = filesList.get(minString);
			//System.out.println(filesList.toString());
			//to get all the posting lists for the current string
			StringBuilder temp = new StringBuilder();
			//now iterate over the fList, get the fileIds and append their posting lists 
			//System.out.println(fList);

			
			for(Integer fId: fList)
			{
				temp.append(filePostings.get(fId));
				temp.append(" ");
			}
			writer.print(minString+":");
			writer.print(temp.toString());
			writer.println();	

			//update structures
			filesList.remove(minString);


			//now for all these files read the next line from the file fId
			//get string and postings and update structures

			//System.out.println(fList);
			for(Integer fId: fList)
			{
				if(ob.isEmpty.get(fId)==1)
				{
					curLine = ob.getLine(ob.readers.get(fId),fId);
				}
				else
				{
					continue;
				}

				//System.out.println(i+"  "+curLine);
				String[] slist = curLine.split(":");
				//System.out.println(slist[0]+ " ");
				

				//add current string to treeset
				currentStrings.add(slist[0]);

				//check if there is entry for the current string
				//if entry is already there, add current fileId to its list

				ArrayList<Integer> fList1; //temp var
				if(filesList.containsKey(slist[0]))
				{
					fList1 = filesList.get(slist[0]);
					fList1.add(fId);

				}

				//if not create a new entry for the string
				else
				{
					fList1 = new ArrayList<Integer>();
					fList1.add(fId);
					filesList.put(slist[0], fList1);
				}

				//update hashmap ie add new list

				
				//update postings
				filePostings.add(fId, slist[1]);
			}
			//System.out.println(filesList);
		}
		writer.close();
	}
}