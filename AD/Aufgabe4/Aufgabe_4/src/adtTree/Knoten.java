package adtTree;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

public class Knoten {
	
	private int key;
	
	Knoten lSohn;
	Knoten rSohn;
	Knoten vater;
	
	int hoehe;
	int balance;
	
	Knoten(int key){
		this.key = key;
		this.balance = 0;
		this.hoehe = 1;
	}
	
	public int getMax(){
		if(!this.hasrSohn()){
			return this.key;
		}else{
			return this.rSohn.getMax();
		}
	}
	
	public int getHoehe(){
		return this.hoehe;
	}
	
	public int getBalance(){
		
		int lHoehe = 0;
		int rHoehe = 0;
		if(this.lSohn != null){
			lHoehe = this.lSohn.getHoehe();
		}
		
		if(this.rSohn != null){
			rHoehe = this.rSohn.getHoehe();
		}
		
		int result = rHoehe - lHoehe;
		return result;
	}
	
	
	
	public int getKey(){
		return this.key;
	}
	
	public boolean haslSohn(){
		return this.lSohn != null;
	}
	
	public boolean hasrSohn(){
		return this.rSohn != null;
	}
	
	public boolean hasvater(){
		return this.vater != null;
	}
	
	public void setKey(int elem){
		this.key = elem;
	}
	
	public void setlSohn(Knoten k){
		this.lSohn = k;
	}
	
	public void setrSohn(Knoten k){
		this.rSohn = k;
	}
	
	public void setVater(Knoten k){
		this.vater = k;
	}
	
	public Knoten updateHoeheAdd(){
		
					
		int lHoehe = 0;
		int rHoehe = 0;
		if(this.lSohn != null){
			lHoehe = this.lSohn.getHoehe();
		}
		
		if(this.rSohn != null){
			rHoehe = this.rSohn.getHoehe();
		}
		
		this.setHoehe(1 + adtTree.HilfsFunktion.max(rHoehe, lHoehe));
		
		Knoten unBal = null;
		int bal = this.getBalance();
		if(bal == -2 || bal == 2){
			unBal = this;
			return this;
		}
			
		if(this.vater != null){
			Knoten newunbal = this.vater.updateHoeheAdd();
			if(unBal == null && newunbal != null) unBal = newunbal;
		}
		return unBal;
	}
	
//	public Knoten updateHoeheKeinSohn(boolean isLeftSohn){
//		Knoten unbal = null;
//		
//		int sohnHoehe = 0;
//		
//		if(isLeftSohn && this.rSohn != null){
//			sohnHoehe = HilfsFunktion.hoehe(this.rSohn);
//			this.setHoehe(sohnHoehe + 1);
//			if(this.hasvater()) this.vater.updateHoeheSub(isLeftSohn);
//		}
//		
//		return unbal;
//	}
	
	
	public void setHoehe(int hoehe) {
		this.hoehe = hoehe;
	}
	
	public String toString(){
		return "Key:	"+key;
	}
}




