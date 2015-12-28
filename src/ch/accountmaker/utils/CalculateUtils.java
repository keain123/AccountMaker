package ch.accountmaker.utils;

public class CalculateUtils {

	/**
	 * 把字符串分隔为所需要的数组
	 * 
	 * @param all eg.100 200 300 = 600X12 = 7200
	 * @return eg. {"100", "200", "300"}
	 */
	public String[] deviceString(String all) {
		int index = -1;
		if ((index = all.indexOf("=")) >= 0) {
			all = all.substring(0, index);
		}

		String[] temps = all.split(" ");
		return temps;
	}
	
}
