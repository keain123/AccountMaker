package ch.accountmaker.model.eventbus;

public class YearMonth {

	final public static int BEGIN = 0;
	final public static int END = 1;
	
	private int docId;
	
	private int year;
	
	private int month;
	
	private int type;

	public YearMonth(int year, int month, int type) {
		this.year = year;
		this.month = month;
		this.type = type;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}
	
}
