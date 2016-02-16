package reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReservationControl {
	//この予約システムのユーザIDとログイン状態
	String reservation_userid;
	private boolean flagLogin;
	
	//ログインしていればtrue
	ReservationControl(){
		flagLogin = false;
	}
	//指定した日,施設の 空き状況(というか予約状況)
	public String getReservationOn( String facility, String ryear_str, String rmonth_str, String rday_str){
		String res = "";
		
		// 年月日が数字かどうかををチェックする処理
		try {
			int ryear = Integer.parseInt( ryear_str);
			int rmonth = Integer.parseInt( rmonth_str);
			int rday = Integer.parseInt( rday_str);
		} catch(NumberFormatException e){
			res ="年月日には数字を指定してください";
			return res;
		}
		res = facility + " 予約状況\n\n";

		// 月と日が一桁だったら,前に0をつける処理
		if (rmonth_str.length()==1) {
			rmonth_str = "0" + rmonth_str;
		}
		if ( rday_str.length()==1){
			rday_str = "0" + rday_str;
		}
		//SQL で検索するための年月日のフォーマットの文字列を作成する処理
		String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;

		//(1) MySQL を使用する準備
		//connectDB();
		MySQL mysql = new MySQL();

		//(2) MySQLの操作(SELECT文の実行)
		try {
			// 予約情報を取得するクエリ
			ResultSet rs = mysql.getReservation(rdate, facility);
			boolean exist = false;
			while(rs.next()){
				String start = rs.getString("start_time");
				String end = rs.getString("end_time");
				res += " " + start + " -- " + end + "\n";
				exist = true;
			}
			if ( !exist){ //予約が1つも存在しない場合の処理
				res = "予約はありません";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	//////ログイン・ログアウトボタンの処理
	public String loginLogout( MainFrame frame){
		String res=""; //結果を入れる変数
		if ( flagLogin){
			//ログアウトを行う処理
			flagLogin = false;
			frame.buttonLog.setLabel(" ログイン ");
		} else {
			//ログインを行う処理
			
			//ログインダイアログの生成と表示
			LoginDialog ld = new LoginDialog(frame);
			ld.setVisible(true);
			ld.setModalityType(LoginDialog.ModalityType.APPLICATION_MODAL);
			//IDとパスワードの入力がキャンセルされたら,空文字列を結果として終了
			if ( ld.canceled){
				return "";
			}

			//ユーザIDとパスワードが入力された場合の処理
			//ユーザIDは他の機能のときに使用するのでメンバー変数に代入
			reservation_userid = ld.tfUserID.getText();
			//パスワードはここでしか使わないので,ローカル変数に代入
			String password = ld.tfPassword.getText();
			
			//(2) MySQLの操作(SELECT文の実行)
			try { // userの情報を取得するクエリ
				MySQL mysql = new MySQL();
				ResultSet rs = mysql.getLogin(reservation_userid); 
				if (rs.next()){
					rs.getString("password");
					String password_from_db = rs.getString("password");
					if ( password_from_db.equals(password)){
						//認証成功:データベースのIDとパスワードに一致
						flagLogin = true;
						frame.buttonLog.setLabel("ログアウト");
						res = "";
					}else {
						//認証失敗:パスワードが不一致
						res = "ログインできません。IDかパスワードが違います。";
					}
				} else {
					//認証失敗;ユーザIDがデータベースに存在しない
					res = "ログインできません。IDかパスワードが違います。";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return res;
	}	
	private boolean checkReservationDate( int y, int m, int d){
		// 予約日
		Calendar dateR = Calendar.getInstance();
		dateR.set( y, m-1, d);	// 月から1引かなければならないことに注意！

		// 今日の１日後
		Calendar date1 = Calendar.getInstance();
		date1.add(Calendar.DATE, 1);

		// 今日の３ヶ月後（90日後)
		Calendar date2 = Calendar.getInstance();
		date2.add(Calendar.DATE, 90);

		if ( dateR.after(date1) && dateR.before(date2)){
			return true;
		}
		return false;
	}
	//////新規予約の登録
	public String makeReservation(MainFrame frame){

		String res="";		//結果を入れる変数

		if ( flagLogin){ // ログインしていた場合
			//新規予約画面作成
			ReservationDialog rd = new ReservationDialog(frame);

			// 新規予約画面の予約日に，メイン画面に設定されている年月日を設定する
			rd.tfYear.setText(frame.tfYear.getText());
			rd.tfMonth.setText(frame.tfMonth.getText());
			rd.tfDay.setText(frame.tfDay.getText());

			// 新規予約画面を可視化
			rd.setVisible(true);
			if ( rd.canceled){
				return res;
			}
			try {
				//新規予約画面から年月日を取得
				String ryear_str = rd.tfYear.getText();
				String rmonth_str = rd.tfMonth.getText();
				String rday_str = rd.tfDay.getText();

				// 年月日が数字かどうかををチェックする処理
				int ryear = Integer.parseInt( ryear_str);
				int rmonth = Integer.parseInt( rmonth_str);
				int rday = Integer.parseInt( rday_str);

				if ( checkReservationDate( ryear, rmonth, rday)){	// 期間の条件を満たしている場合
					// 新規予約画面から施設名，開始時刻，終了時刻を取得
					String facility = rd.choiceFacility.getSelectedItem();
					String st = rd.startHour.getSelectedItem()+":" + rd.startMinute.getSelectedItem() +":00";
					String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() +":00";

					if( st.equals(et)){		//開始時刻と終了時刻が等しい
						res = "開始時刻と終了時刻が同じです";
					} else {

						try {
							// 月と日が一桁だったら，前に0をつける処理
							if (rmonth_str.length()==1) {
								rmonth_str = "0" + rmonth_str;
							}
							if ( rday_str.length()==1){
								rday_str = "0" + rday_str;
							}
							//(2) MySQLの操作(SELECT文の実行)
							String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
			
							MySQL mysql = new MySQL();
							ResultSet rs = mysql.selectReservation(rdate, facility);
						      // 検索結果に対して重なりチェックの処理
						      boolean ng = false;	//重なりチェックの結果の初期値（重なっていない=false）を設定
							  // 取得したレコード一つ一つに対して確認
						      while(rs.next()){
							  		//レコードの開始時刻と終了時刻をそれぞれstartとendに設定
							        String start = rs.getString("start_time");
							        String end = rs.getString("end_time");

							        if ( (start.compareTo(st)<0 && st.compareTo(end)<0) ||		//レコードの開始時刻＜新規の開始時刻　AND　新規の開始時刻＜レコードの終了時刻
							        	 (st.compareTo(start)<0 && start.compareTo(et)<0)){		//新規の開始時刻＜レコードの開始時刻　AND　レコードの開始時刻＜新規の開始時刻
										 	// 重複有りの場合に ng をtrueに設定
							        	ng = true; break;
							        }
						      }
							  /// 重なりチェックの処理　ここまで  ///////

						      if (!ng){	//重なっていない場合
			
						    	  int rs_int = mysql.setReservation(rdate, st, et, reservation_userid, facility);
						    	  res ="予約されました";
						      } else {	//重なっていた場合
						    	  res = "既にある予約に重なっています";
						      }
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					res = "予約日が無効です．";
				}
			} catch(NumberFormatException e){
				res ="予約日には数字を指定してください";
			}

		} else { // ログインしていない場合
			res = "ログインしてください";
		}
		return res;
	}

	//予約の確認
	public String checkReservation(){
		String res="";		//結果を入れる変数

		if ( flagLogin){ // ログインしていた場合
			try {
				MySQL mysql = new MySQL();
				ResultSet rs = mysql.getReservationId(reservation_userid);
				boolean exist = false;
				while (rs.next()) {
					String date = rs.getString("date");
					String start = rs.getString("start_time");
					String end = rs.getString("end_time");
					String facility = rs.getString("facility_name");
					exist = true;
					res += " "+date+" "+facility+"\n     "+start+" -- "+end +"\n";
				}
				if (!exist) {
					//ユーザIDと一致する予約が存在しない
					res = " " + reservation_userid + " 様の予約はありません";
				} 
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} else { // ログインしていない場合
			res = "ログインしてください";
		}
		return res;
	}

	//施設概要
	public String getExplanationFacility(String facility){
		String res = "";
		MySQL mysql = new MySQL();
		try {
			ResultSet rs = mysql.getFacility(facility);
			if (rs.next()) {
				String ot = rs.getString("open_time");
				String ct = rs.getString("close_time");
				String ex = rs.getString("explanation");
				res = "開始時刻 ： "+ot+"\n終了時刻 ： "+ct+"\n概要\n"+ex;
			} else {
				res = "施設概要が存在しません";
			} 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}
	
	//予約のキャンセル
	public String cancelReservation(MainFrame frame){

		String res="";		//結果を入れる変数

		if ( flagLogin){ // ログインしていた場合
			//予約キャンセル画面作成
			ReservationCancelDialog rcd = new ReservationCancelDialog(frame);

			// 予約キャンセル画面の予約日に，メイン画面に設定されている年月日を設定する
			rcd.tfYear.setText(frame.tfYear.getText());
			rcd.tfMonth.setText(frame.tfMonth.getText());
			rcd.tfDay.setText(frame.tfDay.getText());
			
			// 予約キャンセル画面を可視化
			rcd.setVisible(true);
			if ( rcd.canceled){
				return res;
			}
			try {
				//予約キャンセル画面から予約データをそれぞれ取得
				String ryear_str = rcd.tfYear.getText();
				String rmonth_str = rcd.tfMonth.getText();
				String rday_str = rcd.tfDay.getText();

				// 年月日が数字かどうかををチェックする処理
				int ryear = Integer.parseInt( ryear_str);
				int rmonth = Integer.parseInt( rmonth_str);
				int rday = Integer.parseInt( rday_str);

				if ( checkReservationDate( ryear, rmonth, rday)){	// 期間の条件を満たしている場合
					// 予約キャンセル画面から施設名，開始時刻，終了時刻を取得
					String facility = rcd.choiceFacility.getSelectedItem();
					String st = rcd.startHour.getSelectedItem()+":" + rcd.startMinute.getSelectedItem() +":00";
					String et = rcd.endHour.getSelectedItem() + ":" + rcd.endMinute.getSelectedItem() +":00";

					if( st.equals(et)){		//開始時刻と終了時刻が等しい
						res = "開始時刻と終了時刻が同じです";
					} else {

						try {
							// 月と日が一桁だったら，前に0をつける処理
							if (rmonth_str.length()==1) {
								rmonth_str = "0" + rmonth_str;
							}
							if ( rday_str.length()==1){
								rday_str = "0" + rday_str;
							}
							
							//MySQLの操作
							String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
							
							MySQL mysql = new MySQL();
							ResultSet rs = mysql.deletechcekReservation(rdate, st, et, reservation_userid, facility);
							if (rs.next()){
								mysql.deleteReservation(rdate,st,et,reservation_userid,facility);
								res = "予約のキャンセルが完了しました";
							} else {
								res = "予約が存在しません";
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					res = "予約日が無効です．";
				}
			} catch(NumberFormatException e){
				res ="予約日には数字を指定してください";
			}

				
		} else { // ログインしていない場合
			res = "ログインしてください";
		}
		return res;
	}
	
}
