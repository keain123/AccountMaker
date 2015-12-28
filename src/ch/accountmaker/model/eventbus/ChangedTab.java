package ch.accountmaker.model.eventbus;

/**
 * 修改tab标题，用于EventBus
 * @author chh
 *
 */
public class ChangedTab {

	String oldTitle;
	
	String newTitle;
	
	public ChangedTab(String oldT, String newT) {
		oldTitle = oldT;
		newTitle = newT;
	}

	public String getOldTitle() {
		return oldTitle;
	}

	public void setOldTitle(String oldTitle) {
		this.oldTitle = oldTitle;
	}

	public String getNewTitle() {
		return newTitle;
	}

	public void setNewTitle(String newTitle) {
		this.newTitle = newTitle;
	}
	
	
	
}
