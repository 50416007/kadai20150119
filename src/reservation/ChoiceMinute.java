package reservation;

import java.awt.Choice;

public class ChoiceMinute extends Choice{
		
	public ChoiceMinute(){
		// 00•ª‚Æ30•ª‚Ì‚İ‚ğ“o˜^
		add("00");
		add("30");
	}

	//‘I‘ğ‚³‚ê‚Ä‚¢‚é•ª‚ğ®”‚Å•Ô‚·
	public int getMinute(int index){
		if ( index==0){
			return 0;
		} else {
			return 30;
		}
	}



}
