package ch.accountmaker.model.eventbus;

public class UploadFlag {
	
	private boolean isSucc;
	
	private String msg;
	
	private int totalCount;
	
	private int thisCount;
	
	public UploadFlag(boolean isSucc, String msg, int totalCount, int thisCount) {
		this.isSucc = isSucc;
		this.msg = msg;
		this.totalCount = totalCount;
		this.thisCount = thisCount;
	}

	public boolean isSucc() {
		return isSucc;
	}

	public void setSucc(boolean isSucc) {
		this.isSucc = isSucc;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getThisCount() {
		return thisCount;
	}

	public void setThisCount(int thisCount) {
		this.thisCount = thisCount;
	}

	
}
