package cn.hisdar.computerremote.debug;

public class DebugerTimeData {

	private String activeName;
	private long timeValue;
	
	public DebugerTimeData() {
		
	}
	
	public DebugerTimeData(String actionName, long timeValue) {
		this.activeName = actionName;
		this.timeValue = timeValue;
	}

	public String getActiveName() {
		return activeName;
	}

	public void setActiveName(String activeName) {
		this.activeName = activeName;
	}

	public long getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(long timeValue) {
		this.timeValue = timeValue;
	}
	
	public String toXmlString() {
		return 	"<" + activeName + ">" +
				timeValue +
				"</" + activeName + ">";
	}
}
