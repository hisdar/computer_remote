package cn.hisdar.cr.event;

import cn.hisdar.lib.adapter.IntegerAdapter;

public class HKeyEvent {

	public int keyCode;
	public int keyValue;
	
	public void setKeyCode(int keycode) {
		this.keyCode = keycode;
	}
	
	public int getKeyCode() {
		return keyCode;
	}

	public int getKeyValue() {
		return keyValue;
	}
	
	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = IntegerAdapter.parseInt(keyCode, -1);
	}
	
	public void setKeyValue(String keyValue) {
		this.keyValue = IntegerAdapter.parseInt(keyValue, -1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + keyCode;
		result = prime * result + keyValue;
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
		HKeyEvent other = (HKeyEvent) obj;
		if (keyCode != other.keyCode)
			return false;
		if (keyValue != other.keyValue)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HKeyEvent [keyCode=" + keyCode + ", keyValue=" + keyValue + "]";
	}
	
}
