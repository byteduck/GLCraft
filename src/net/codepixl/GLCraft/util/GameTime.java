package net.codepixl.GLCraft.util;

public class GameTime{

	private static final int HOURLENGTH = Constants.dayLengthMS/24;
	private static final int MINUTELENGTH = Constants.dayLengthMS/24/60;
	private static final int SECONDLENGTH = Constants.dayLengthMS/24/60/60;
	
	private int hours;
	private int minutes;
	private int seconds;
	private int days;
	private long timestamp;
	
	public GameTime(long millis){
		this.timestamp = millis;
		int rtime = (int) (millis % Constants.dayLengthMS);
		this.setHours(rtime/HOURLENGTH);
		this.setMinutes((rtime/MINUTELENGTH) % 60);
		this.setSeconds((rtime/SECONDLENGTH) % 60);
		this.setDays((int)(millis/Constants.dayLengthMS));
	}
	
	public void updateTime(long millis){
		this.timestamp = millis;
		int rtime = (int) (millis % Constants.dayLengthMS);
		this.setHours(rtime/HOURLENGTH);
		this.setMinutes((rtime/MINUTELENGTH) % 60);
		this.setSeconds((rtime/SECONDLENGTH) % 60);
		this.setDays((int)(millis/Constants.dayLengthMS));
	}

	public int getHours() {
		return hours;
	}

	private void setHours(int hours) {
		this.hours = hours;
	}

	public int getMinutes() {
		return minutes;
	}

	private void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	private void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getDays() {
		return days;
	}

	private void setDays(int days) {
		this.days = days;
	}
	
	public long getTimeStamp(){
		return this.timestamp;
	}
	
	@Override
	public String toString(){
		return this.hours+":"+this.minutes+":"+this.seconds+" Day "+this.days;
	}
	
	public String toString(TimeFormat format){
		String ret = "";
		if(format.hours)
			ret+=hours;
		if(format.minutes)
			ret+=":"+minutes;
		if(format.seconds)
			ret+=":"+seconds;
		if(format.days)
			ret+=" Day "+days;
		return ret;
	}
	
}
