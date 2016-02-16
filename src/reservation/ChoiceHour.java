package reservation;

import java.awt.Choice;

public class ChoiceHour extends Choice{
	ChoiceHour(){
		//時刻の範囲の初期値は 9時～21時とする
		resetRange(9,21);
	}

	public void resetRange( int start, int end){
		// 範囲に含まれる時刻の数を求める
		int number = end - start +1;
		// 選択ボックスの内容をクリアする
		removeAll();
		// 指定できる範囲の時刻を設定する
		while (start<=end){
			String h = String.valueOf(start);
			//一桁の場合,前に0を付ける
			if ( h.length()==1){
				h = "0" + h;
			}
			// 選択ボックスに追加 (こちらは文字列)
			add(h);
			// startを1増やす
			start++;
		}
	}

	// 最後に設定されている時刻を返す
	public String getLast(){
		return getItem( getItemCount()-1 );
	}
}
