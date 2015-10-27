package com.example.myfirstrgb;

public class Restaurant {
	private String name="";
	private String address="";
	private int control_add1=0;
	private int control_add2=0;
	private int control_add3=1;
	private int prod_id = 1;
	private int prod_type = 1;
	private String type="";
	private String notes="";
	private int lightstate = 0;

	
	public String getName() {
		return(name);
	}
	
	public void setName(String name) {
		this.name=name;
	}
	public int getstate() {
		return(lightstate);
	}	
	public void setstate(int lightstate){
		this.lightstate = lightstate;
	}
	public String getAddress() {
		return(address);
	}
	public int getcontrol_add1()
	{
		return(control_add1);
	}
	public int getcontrol_add2()
	{
		return(control_add2);
	}
	public int getcontrol_add3()
	{
		return(control_add3);
	}
	public int getprod_id()
	{
		return(prod_id);
	}
	public int getprod_type()
	{
		return(prod_type);
	}
	
	public void setAddress(String address) {
		this.address=address;
	}
	public void setControl_add1(int control_add1)
	{
		this.control_add1=control_add1;
	}
	public void setControl_add2(int control_add2)
	{
		this.control_add2=control_add2;
	}
	public void setControl_add3(int control_add3)
	{
		this.control_add3=control_add3;
	}
	public void setProd_id(int prod_id)
	{
		this.prod_id = prod_id;
	}
	public void setProd_type(int prod_type)
	{
		this.prod_type = prod_type;
	}
	public String getType() {
		return(type);
	}
	
	public void setType(String type) {
		this.type=type;
	}
	
	public String getNotes() {
		return(notes);
	}
	
	public void setNotes(String notes) {
		this.notes=notes;
	}
	
	public String toString() {
		return(getName());
	}
}