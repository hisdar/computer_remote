package cn.hisdar.cr.event;

import cn.hisdar.lib.adapter.FloatAdapter;
import cn.hisdar.lib.adapter.IntegerAdapter;

public class Pointer {

	private int id;
	private float x;
	private float y;
	private String toolType;
	
	public Pointer() {
		
	}
	
	public Pointer(Pointer pointer) {
		x = pointer.x;
		y = pointer.y;
		id = pointer.id;
		toolType = new String(pointer.toolType);
	}

	public void setX(String xString) {
		x = FloatAdapter.parseFloat(xString, -1);
	}
	
	public void setY(String yString) {
		y = FloatAdapter.parseFloat(yString, -1);
	}
	
	public void setId(String idString) {
		id = IntegerAdapter.parseInt(idString, -1);
	}
	
	public void setToolType(String toolType) {
		this.toolType = toolType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public String getToolType() {
		return toolType;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((toolType == null) ? 0 : toolType.hashCode());
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		Pointer other = (Pointer) obj;
		if (id != other.id)
			return false;
		if (toolType == null) {
			if (other.toolType != null)
				return false;
		} else if (!toolType.equals(other.toolType))
			return false;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Pointer [id=" + id + ", x=" + x + ", y=" + y + ", toolType=" + toolType + "]";
	}
}
