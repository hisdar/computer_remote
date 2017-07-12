package cn.hisdar.cr.event;

import cn.hisdar.lib.adapter.IntegerAdapter;

public class HMouseEvent {

	public int buttonId;
	public int value;
	
	public int getButtonId() {
		return buttonId;
	}
	
	public void setButtonId(int buttonId) {
		this.buttonId = buttonId;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "HMouseEvent [buttonId=" + buttonId + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + buttonId;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HMouseEvent other = (HMouseEvent) obj;
		if (buttonId != other.buttonId)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	public void setButtonId(String buttonId2) {
		buttonId = IntegerAdapter.parseInt(buttonId2, -1);
	}
	
	public void setValue(String value) {
		this.value = IntegerAdapter.parseInt(value, -1);
	}
	
}
