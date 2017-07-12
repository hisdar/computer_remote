package cn.hisdar.cr.debug;

import java.util.ArrayList;

public class DebugerTimeDataContainer {

	private ArrayList<DebugerTimeData> timeDatas;
	
	public DebugerTimeDataContainer() {
		timeDatas = new ArrayList<>();
	}
	
	public void addTimeData(String actionName, long timeValue) {
		timeDatas.add(new DebugerTimeData(actionName, timeValue));
	}
	
	public void addTimeData(DebugerTimeData timeData) {
		timeDatas.add(timeData);
	}

	public ArrayList<DebugerTimeData> getTimeDatas() {
		return timeDatas;
	}

	public void setTimeDatas(ArrayList<DebugerTimeData> timeDatas) {
		this.timeDatas = timeDatas;
	}

	public String toXmlString() {
		StringBuilder xmlString = new StringBuilder();
		
		xmlString.append("<server-time-data>\n");
		for (int i = 0; i < timeDatas.size(); i++) {
			xmlString.append("\t");
			xmlString.append(timeDatas.get(i).toXmlString());
			xmlString.append("\n");
		}
		
		xmlString.append("</server-time-data>");
		
		return null;
	}
	
}
