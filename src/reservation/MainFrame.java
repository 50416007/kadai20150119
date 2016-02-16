package reservation;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFrame extends Frame implements ActionListener,WindowListener,KeyListener {
	
	ReservationControl reservationControl;
	
	Panel panelNorth; 		//上部パネル
	Panel panelNorthSub1; 	//上部パネルの上
	Panel panelNorthSub2; 	//上部パネルの中央
	Panel panelNorthSub3; 	//上部パネルの下
	Panel panelMid;			//中央パネル
	Panel panelSouth;		//下部パネル

	Button buttonLog;		// ログイン ・ ログアウト ボタン
	Button buttonExplanation;	// 施設概要 説明ボタン
	Button buttonVacancy;	// 空き状況確認
	Button buttonReservation;	// 新規予約ボタン
	Button buttonConfirm;	// 予約の確認
	Button buttonCancel;	// 予約のキャンセルボタン

	ChoiceFacility choiceFacility;	// 施設選択用選択ボックス
	TextField tfYear, tfMonth, tfDay;	//年月日のテキストフィールド
	TextArea textMessage;	// 結果表示用メッセージ欄
	
	public MainFrame(ReservationControl reservationControl) throws HeadlessException {
		this.reservationControl = reservationControl;
		addWindowListener(this);
		addKeyListener(this);
		
		//ボタンの生成
		buttonLog = new Button("ログイン");
		buttonExplanation = new Button(" 施設概要");
		buttonVacancy = new Button(" 空き状況確認");
		buttonReservation = new Button(" 新規予約");
		buttonConfirm = new Button(" 予約の確認");
		buttonCancel = new Button(" 予約のキャンセル");
		
		// 設備チョイスボックスの生成
		choiceFacility = new ChoiceFacility();
		tfYear = new TextField("",4);
		tfMonth = new TextField("",2);
		tfDay = new TextField("",2);

		// 上中下のパネルを使うため,レイアウトマネージャーにBorderLayoutを設定
		setLayout( new BorderLayout());

		// 上部パネルの上パネルに 予約システム というラベルと [ログイン]ボタンを追加
		panelNorthSub1 = new Panel();
		panelNorthSub1.add(new Label("施設予約システム "));
		panelNorthSub1.add(buttonLog);

		// 上部パネルの中央パネルに 施設[施設名選択]チョイス[概要説明]ボタンを追加
		panelNorthSub2 = new Panel();
		panelNorthSub2.add(new Label("施設 "));
		panelNorthSub2.add( choiceFacility);
		panelNorthSub2.add(new Label(" "));
		panelNorthSub2.add( buttonExplanation);
		
		// 上部パネルの下パネルに年月日入力欄と 空き状況確認ボタンを追加
		panelNorthSub3 = new Panel();
		panelNorthSub3.add(new Label(" "));
		panelNorthSub3.add(tfYear);
		panelNorthSub3.add(new Label("年"));
		panelNorthSub3.add(tfMonth);
		panelNorthSub3.add(new Label("月"));
		panelNorthSub3.add(tfDay);
		panelNorthSub3.add(new Label("日 "));
		panelNorthSub3.add( buttonVacancy);
		
		// 上部パネルに3つのパネルを追加
		panelNorth = new Panel(new BorderLayout());
		panelNorth.add(panelNorthSub1, BorderLayout.NORTH);
		panelNorth.add(panelNorthSub2, BorderLayout.CENTER);
		panelNorth.add(panelNorthSub3, BorderLayout.SOUTH);

		// メイン画面(MainFrame)に上部パネルを追加
		add(panelNorth,BorderLayout.NORTH);
		
		// 中央パネルにテキストメッセージ欄を設定
		panelMid = new Panel();
		textMessage = new TextArea( 20, 80);
		textMessage.setEditable(false);
		panelMid.add(textMessage);

		// メイン画面(MainFrame)に中央パネルを追加
		add( panelMid,BorderLayout.CENTER);
		
		// 下部パネルにボタンを設定
		panelSouth = new Panel();
		panelSouth.add(buttonReservation);
		panelSouth.add(new Label(" "));
		panelSouth.add(buttonConfirm);
		panelSouth.add(new Label(" "));
		panelSouth.add(buttonCancel);

		// メイン画面(MainFrame)に下部パネルを追加
		add( panelSouth,BorderLayout.SOUTH);
		
		//ボタンのアクションリスナの追加
		buttonLog.addActionListener(this);
		buttonExplanation.addActionListener(this);
		buttonVacancy.addActionListener(this);
		buttonReservation.addActionListener(this);
		buttonConfirm.addActionListener(this);
		buttonCancel.addActionListener(this);
		
		addWindowListener(this);
		addKeyListener(this);

	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		System.exit(0);
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String result = new String();
		textMessage.setText("");
		if ( e.getSource() == buttonVacancy){
			//空き状況確認
			result = reservationControl.getReservationOn(choiceFacility.getSelectedItem(), tfYear.getText(), tfMonth.getText(), tfDay.getText());
		} else if (e.getSource() == buttonLog){
			//ログイン・ログアウト
			result = reservationControl.loginLogout(this);
		} else if (e.getSource() == buttonReservation){
			//新規予約
			result = reservationControl.makeReservation(this);
		} else if (e.getSource() == buttonConfirm){
			//予約確認
			result = reservationControl.checkReservation();
		} else if (e.getSource() == buttonExplanation){
			//施設概要
			result = reservationControl.getExplanationFacility(choiceFacility.getSelectedItem());
		} else if (e.getSource() == buttonCancel){
			//予約キャンセル
			result = reservationControl.cancelReservation(this);
		}
		textMessage.setText(result);
	}

}
