import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class BuildSecIndex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//now read each file and create dense secondary index for each file
		String[] fnames = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
		
		BufferedReader reader;
		PrintWriter writer;
		String line;
		
		/*
		try
		{
			for(String name:fnames)
			{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream("rsc/merged"+name+".txt")));
				writer = new PrintWriter("rsc/secondary"+name+".txt");
				
				try 
				{
					int offset=0; //to count of offset of each postingsList
					while((line = reader.readLine()) != null)
					{
						String[] sList = line.split("-");
						writer.println(sList[0]+" "+offset);
						offset+=line.length()+1;		
					}
					reader.close();
					writer.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream("rsc/titles.txt"), Charset.forName("iso-8859-1")));
			writer = new PrintWriter("rsc/titlesSec.txt");
			try 
			{
				int count=1;
				int offset=0;
				writer.println(offset);

				while((line = reader.readLine()) != null)
				{
					if(count%1000==0)
					{
						writer.println(offset);
					}
					offset+= line.codePointCount(0, line.length())+1;
					//offset+=line.length()+1;
					count++;
				}
				reader.close();
				writer.close();
				System.out.println(count);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
