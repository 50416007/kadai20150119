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
	//���̗\��V�X�e���̃��[�UID�ƃ��O�C�����
	String reservation_userid;
	private boolean flagLogin;
	
	//���O�C�����Ă����true
	ReservationControl(){
		flagLogin = false;
	}
	//�w�肵����,�{�݂� �󂫏�(�Ƃ������\���)
	public String getReservationOn( String facility, String ryear_str, String rmonth_str, String rday_str){
		String res = "";
		
		// �N�������������ǂ��������`�F�b�N���鏈��
		try {
			int ryear = Integer.parseInt( ryear_str);
			int rmonth = Integer.parseInt( rmonth_str);
			int rday = Integer.parseInt( rday_str);
		} catch(NumberFormatException e){
			res ="�N�����ɂ͐������w�肵�Ă�������";
			return res;
		}
		res = facility + " �\���\n\n";

		// ���Ɠ����ꌅ��������,�O��0�����鏈��
		if (rmonth_str.length()==1) {
			rmonth_str = "0" + rmonth_str;
		}
		if ( rday_str.length()==1){
			rday_str = "0" + rday_str;
		}
		//SQL �Ō������邽�߂̔N�����̃t�H�[�}�b�g�̕�������쐬���鏈��
		String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;

		//(1) MySQL ���g�p���鏀��
		//connectDB();
		MySQL mysql = new MySQL();

		//(2) MySQL�̑���(SELECT���̎��s)
		try {
			// �\������擾����N�G��
			ResultSet rs = mysql.getReservation(rdate, facility);
			boolean exist = false;
			while(rs.next()){
				String start = rs.getString("start_time");
				String end = rs.getString("end_time");
				res += " " + start + " -- " + end + "\n";
				exist = true;
			}
			if ( !exist){ //�\��1�����݂��Ȃ��ꍇ�̏���
				res = "�\��͂���܂���";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	//////���O�C���E���O�A�E�g�{�^���̏���
	public String loginLogout( MainFrame frame){
		String res=""; //���ʂ�����ϐ�
		if ( flagLogin){
			//���O�A�E�g���s������
			flagLogin = false;
			frame.buttonLog.setLabel(" ���O�C�� ");
		} else {
			//���O�C�����s������
			
			//���O�C���_�C�A���O�̐����ƕ\��
			LoginDialog ld = new LoginDialog(frame);
			ld.setVisible(true);
			ld.setModalityType(LoginDialog.ModalityType.APPLICATION_MODAL);
			//ID�ƃp�X���[�h�̓��͂��L�����Z�����ꂽ��,�󕶎�������ʂƂ��ďI��
			if ( ld.canceled){
				return "";
			}

			//���[�UID�ƃp�X���[�h�����͂��ꂽ�ꍇ�̏���
			//���[�UID�͑��̋@�\�̂Ƃ��Ɏg�p����̂Ń����o�[�ϐ��ɑ��
			reservation_userid = ld.tfUserID.getText();
			//�p�X���[�h�͂����ł����g��Ȃ��̂�,���[�J���ϐ��ɑ��
			String password = ld.tfPassword.getText();
			
			//(2) MySQL�̑���(SELECT���̎��s)
			try { // user�̏����擾����N�G��
				MySQL mysql = new MySQL();
				ResultSet rs = mysql.getLogin(reservation_userid); 
				if (rs.next()){
					rs.getString("password");
					String password_from_db = rs.getString("password");
					if ( password_from_db.equals(password)){
						//�F�ؐ���:�f�[�^�x�[�X��ID�ƃp�X���[�h�Ɉ�v
						flagLogin = true;
						frame.buttonLog.setLabel("���O�A�E�g");
						res = "";
					}else {
						//�F�؎��s:�p�X���[�h���s��v
						res = "���O�C���ł��܂���BID���p�X���[�h���Ⴂ�܂��B";
					}
				} else {
					//�F�؎��s;���[�UID���f�[�^�x�[�X�ɑ��݂��Ȃ�
					res = "���O�C���ł��܂���BID���p�X���[�h���Ⴂ�܂��B";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return res;
	}	
	private boolean checkReservationDate( int y, int m, int d){
		// �\���
		Calendar dateR = Calendar.getInstance();
		dateR.set( y, m-1, d);	// ������1�����Ȃ���΂Ȃ�Ȃ����Ƃɒ��ӁI

		// �����̂P����
		Calendar date1 = Calendar.getInstance();
		date1.add(Calendar.DATE, 1);

		// �����̂R������i90����)
		Calendar date2 = Calendar.getInstance();
		date2.add(Calendar.DATE, 90);

		if ( dateR.after(date1) && dateR.before(date2)){
			return true;
		}
		return false;
	}
	//////�V�K�\��̓o�^
	public String makeReservation(MainFrame frame){

		String res="";		//���ʂ�����ϐ�

		if ( flagLogin){ // ���O�C�����Ă����ꍇ
			//�V�K�\���ʍ쐬
			ReservationDialog rd = new ReservationDialog(frame);

			// �V�K�\���ʂ̗\����ɁC���C����ʂɐݒ肳��Ă���N������ݒ肷��
			rd.tfYear.setText(frame.tfYear.getText());
			rd.tfMonth.setText(frame.tfMonth.getText());
			rd.tfDay.setText(frame.tfDay.getText());

			// �V�K�\���ʂ�����
			rd.setVisible(true);
			if ( rd.canceled){
				return res;
			}
			try {
				//�V�K�\���ʂ���N�������擾
				String ryear_str = rd.tfYear.getText();
				String rmonth_str = rd.tfMonth.getText();
				String rday_str = rd.tfDay.getText();

				// �N�������������ǂ��������`�F�b�N���鏈��
				int ryear = Integer.parseInt( ryear_str);
				int rmonth = Integer.parseInt( rmonth_str);
				int rday = Integer.parseInt( rday_str);

				if ( checkReservationDate( ryear, rmonth, rday)){	// ���Ԃ̏����𖞂����Ă���ꍇ
					// �V�K�\���ʂ���{�ݖ��C�J�n�����C�I���������擾
					String facility = rd.choiceFacility.getSelectedItem();
					String st = rd.startHour.getSelectedItem()+":" + rd.startMinute.getSelectedItem() +":00";
					String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() +":00";

					if( st.equals(et)){		//�J�n�����ƏI��������������
						res = "�J�n�����ƏI�������������ł�";
					} else {

						try {
							// ���Ɠ����ꌅ��������C�O��0�����鏈��
							if (rmonth_str.length()==1) {
								rmonth_str = "0" + rmonth_str;
							}
							if ( rday_str.length()==1){
								rday_str = "0" + rday_str;
							}
							//(2) MySQL�̑���(SELECT���̎��s)
							String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
			
							MySQL mysql = new MySQL();
							ResultSet rs = mysql.selectReservation(rdate, facility);
						      // �������ʂɑ΂��ďd�Ȃ�`�F�b�N�̏���
						      boolean ng = false;	//�d�Ȃ�`�F�b�N�̌��ʂ̏����l�i�d�Ȃ��Ă��Ȃ�=false�j��ݒ�
							  // �擾�������R�[�h���ɑ΂��Ċm�F
						      while(rs.next()){
							  		//���R�[�h�̊J�n�����ƏI�����������ꂼ��start��end�ɐݒ�
							        String start = rs.getString("start_time");
							        String end = rs.getString("end_time");

							        if ( (start.compareTo(st)<0 && st.compareTo(end)<0) ||		//���R�[�h�̊J�n�������V�K�̊J�n�����@AND�@�V�K�̊J�n���������R�[�h�̏I������
							        	 (st.compareTo(start)<0 && start.compareTo(et)<0)){		//�V�K�̊J�n���������R�[�h�̊J�n�����@AND�@���R�[�h�̊J�n�������V�K�̊J�n����
										 	// �d���L��̏ꍇ�� ng ��true�ɐݒ�
							        	ng = true; break;
							        }
						      }
							  /// �d�Ȃ�`�F�b�N�̏����@�����܂�  ///////

						      if (!ng){	//�d�Ȃ��Ă��Ȃ��ꍇ
			
						    	  int rs_int = mysql.setReservation(rdate, st, et, reservation_userid, facility);
						    	  res ="�\�񂳂�܂���";
						      } else {	//�d�Ȃ��Ă����ꍇ
						    	  res = "���ɂ���\��ɏd�Ȃ��Ă��܂�";
						      }
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					res = "�\����������ł��D";
				}
			} catch(NumberFormatException e){
				res ="�\����ɂ͐������w�肵�Ă�������";
			}

		} else { // ���O�C�����Ă��Ȃ��ꍇ
			res = "���O�C�����Ă�������";
		}
		return res;
	}

	//�\��̊m�F
	public String checkReservation(){
		String res="";		//���ʂ�����ϐ�

		if ( flagLogin){ // ���O�C�����Ă����ꍇ
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
					//���[�UID�ƈ�v����\�񂪑��݂��Ȃ�
					res = " " + reservation_userid + " �l�̗\��͂���܂���";
				} 
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} else { // ���O�C�����Ă��Ȃ��ꍇ
			res = "���O�C�����Ă�������";
		}
		return res;
	}

	//�{�݊T�v
	public String getExplanationFacility(String facility){
		String res = "";
		MySQL mysql = new MySQL();
		try {
			ResultSet rs = mysql.getFacility(facility);
			if (rs.next()) {
				String ot = rs.getString("open_time");
				String ct = rs.getString("close_time");
				String ex = rs.getString("explanation");
				res = "�J�n���� �F "+ot+"\n�I������ �F "+ct+"\n�T�v\n"+ex;
			} else {
				res = "�{�݊T�v�����݂��܂���";
			} 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}
	
	//�\��̃L�����Z��
	public String cancelReservation(MainFrame frame){

		String res="";		//���ʂ�����ϐ�

		if ( flagLogin){ // ���O�C�����Ă����ꍇ
			//�\��L�����Z����ʍ쐬
			ReservationCancelDialog rcd = new ReservationCancelDialog(frame);

			// �\��L�����Z����ʂ̗\����ɁC���C����ʂɐݒ肳��Ă���N������ݒ肷��
			rcd.tfYear.setText(frame.tfYear.getText());
			rcd.tfMonth.setText(frame.tfMonth.getText());
			rcd.tfDay.setText(frame.tfDay.getText());
			
			// �\��L�����Z����ʂ�����
			rcd.setVisible(true);
			if ( rcd.canceled){
				return res;
			}
			try {
				//�\��L�����Z����ʂ���\��f�[�^�����ꂼ��擾
				String ryear_str = rcd.tfYear.getText();
				String rmonth_str = rcd.tfMonth.getText();
				String rday_str = rcd.tfDay.getText();

				// �N�������������ǂ��������`�F�b�N���鏈��
				int ryear = Integer.parseInt( ryear_str);
				int rmonth = Integer.parseInt( rmonth_str);
				int rday = Integer.parseInt( rday_str);

				if ( checkReservationDate( ryear, rmonth, rday)){	// ���Ԃ̏����𖞂����Ă���ꍇ
					// �\��L�����Z����ʂ���{�ݖ��C�J�n�����C�I���������擾
					String facility = rcd.choiceFacility.getSelectedItem();
					String st = rcd.startHour.getSelectedItem()+":" + rcd.startMinute.getSelectedItem() +":00";
					String et = rcd.endHour.getSelectedItem() + ":" + rcd.endMinute.getSelectedItem() +":00";

					if( st.equals(et)){		//�J�n�����ƏI��������������
						res = "�J�n�����ƏI�������������ł�";
					} else {

						try {
							// ���Ɠ����ꌅ��������C�O��0�����鏈��
							if (rmonth_str.length()==1) {
								rmonth_str = "0" + rmonth_str;
							}
							if ( rday_str.length()==1){
								rday_str = "0" + rday_str;
							}
							
							//MySQL�̑���
							String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
							
							MySQL mysql = new MySQL();
							ResultSet rs = mysql.deletechcekReservation(rdate, st, et, reservation_userid, facility);
							if (rs.next()){
								mysql.deleteReservation(rdate,st,et,reservation_userid,facility);
								res = "�\��̃L�����Z�����������܂���";
							} else {
								res = "�\�񂪑��݂��܂���";
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					res = "�\����������ł��D";
				}
			} catch(NumberFormatException e){
				res ="�\����ɂ͐������w�肵�Ă�������";
			}

				
		} else { // ���O�C�����Ă��Ȃ��ꍇ
			res = "���O�C�����Ă�������";
		}
		return res;
	}
	
}
