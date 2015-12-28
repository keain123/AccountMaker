package ch.accountmaker.model;

import java.util.Date;

import org.litepal.crud.DataSupport;

public class TimeStamp extends DataSupport {
	
	private Date lastUploadTime;

	private Date lastDownloadTime;

	public Date getLastUploadTime() {
		return lastUploadTime;
	}

	public void setLastUploadTime(Date lastUploadTime) {
		this.lastUploadTime = lastUploadTime;
	}

	public Date getLastDownloadTime() {
		return lastDownloadTime;
	}

	public void setLastDownloadTime(Date lastDownloadTime) {
		this.lastDownloadTime = lastDownloadTime;
	}
	
}
