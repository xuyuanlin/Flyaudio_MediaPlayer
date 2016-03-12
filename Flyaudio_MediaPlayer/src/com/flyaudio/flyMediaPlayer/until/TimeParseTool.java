package com.flyaudio.flyMediaPlayer.until;

public class TimeParseTool {
	/**
	 * 
	 * @param Mesc
	 * @return time
	 */
	public static String MsecParseTime(int Mesc){
		int mescint = Mesc/1000;
		String ss = String.valueOf(mescint%60);
		String mm = String.valueOf(mescint/60);
		if(ss.length()==1)ss = "0" + ss;
		if(mm.length()==1)mm = "0" + mm;
		String time = mm + ":" + ss;
		return time;
	}
	public String TimeParseMesc(String Time){
		
		return Time;
	}
}
