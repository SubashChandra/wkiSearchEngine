
public class MyRanker {
	private int id;
	private double val;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getVal() {
		return val;
	}
	public void setVal(double val) {
		this.val = val;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyRanker other = (MyRanker) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
