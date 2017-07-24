package cn.hisdar.cr.event;

public class PinchPointer {

	private Pointer pointer1;
	private Pointer pointer2;
	
	public PinchPointer() {
		pointer1 = new Pointer();
		pointer2 = new Pointer();
	}
	
	public float getX1() {
		return pointer1.getX();
	}
	public void setX1(float x1) {
		pointer1.setX(x1);
	}
	public float getX2() {
		return pointer2.getX();
	}
	public void setX2(float x2) {
		pointer2.setX(x2);
	}
	public float getY1() {
		return pointer1.getY();
	}
	public void setY1(float y1) {
		pointer1.setY(y1);;
	}
	public float getY2() {
		return pointer2.getY();
	}
	public void setY2(float y2) {
		pointer2.setY(y2);
	}
	public Pointer getPointer1() {
		return pointer1;
	}
	public void setPointer1(Pointer pointer1) {
		this.pointer1 = pointer1;
	}
	public Pointer getPointer2() {
		return pointer2;
	}
	public void setPointer2(Pointer pointer2) {
		this.pointer2 = pointer2;
	}
}
