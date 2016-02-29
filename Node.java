public class Node
{
	long docId;
	long titleCount;
	long infoBoxCount;
	long bodyCount;
	long categoryCount;
	long linksCount;
	long referencesCount;
	long value;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(docId+"-");
		if(titleCount > 0) 
			sb.append("t" + titleCount+";");
		if(infoBoxCount>0)
			sb.append("i" + infoBoxCount+";");
		if(categoryCount>0)
			sb.append("c" + categoryCount+";");
		if(linksCount>0)
			sb.append("l" + linksCount+";");
		if(referencesCount>0)
			sb.append("r" + referencesCount+";");
		if(bodyCount>0)
			sb.append("b" + bodyCount+";");
		//sb.append("v**"+value+";");
		
		sb.append(" ");
		return sb.toString();
	}
		
}