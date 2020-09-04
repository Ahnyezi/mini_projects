package MiniProject2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jdbc.dbconn.DBConn;

public class ServerThread extends Thread {
	private DBConn db;
	private Socket socket;
	private BufferedReader br;
	private PrintWriter out;
	private ObjectInputStream os;
	private ObjectOutputStream oo;
	

	ServerThread(Socket socket) {
		db = DBConn.getInstance();
		createSeq();
		// super();
		this.socket = socket;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			os = new ObjectInputStream(socket.getInputStream());
			oo = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				String msg;
				msg = br.readLine();
				System.out.println(msg);
				if (msg.contains("/stop")){
					System.out.println("Ŭ���̾�Ʈ ����");
					break;
				} else if (msg.contains("idCheck")){
					System.out.println("���̵� üũ");
					out.println(IDCheck(br.readLine()));
				} else if (msg.contains("/insert")){
					System.out.println("ȸ������");
					try {
						User user = (User)os.readObject();
						out.println(insert(user));
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.contains("/login")){	// User login(User user)
					try {	
						User tmp = login((User)os.readObject());				
						oo.writeObject(tmp);
						oo.flush();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.contains("/order")){	// String EnableTime(Order o)
					System.out.println("�ֹ�");
					try {
						String time = EnableTime((Order)os.readObject());
						out.println(time);
						out.println(insert((Order)os.readObject()));
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.contains("/pay")){
					System.out.println("����");
					String num = br.readLine();

					out.println(pay(Integer.parseInt(num)));
				} else if (msg.contains("/PayAvailable")){
					System.out.println("���� ���� ��� ���");
					ArrayList<Order> al;
					try {
						al = payAvailable((User)os.readObject());
						oo.writeObject(al);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.contains("/PrintAll")){	//ArrayList<Order> printAll(User u)
					System.out.println("��ü ���");
					ArrayList<Order> al;
					try {
						al = printAll((User)os.readObject());
						oo.writeObject(al);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.contains("/cancle")){	// boolean cancle(int orderNum)
					System.out.println("���");
					out.println(cancle(Integer.parseInt(br.readLine())));
				} 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} // Ŭ���̾�Ʈ�� ���� ��ȯ (�޽����� �޾ƿ�)

	void createSeq() {
		System.out.println("����");
		Connection conn = db.getConnect();
		String sql = "select * from USER_SEQUENCES where 'SEQ_ORDER' in (SELECT sequence_name FROM USER_SEQUENCES)";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println("seq_order ������ �̹� ����");
				return;
			}
			String sql2 = "create sequence seq_order nocache";
			pstmt = conn.prepareStatement(sql2);
			pstmt.executeUpdate();
			System.out.println("seq_order ������ ���� ����");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("seq_order ������ ���� ����");
			e.printStackTrace();
		}
	}

	public Date transformDate(String date) // string ������ date �������� �ٲٱ�
	{
		SimpleDateFormat beforeFormat = new SimpleDateFormat("yyyymmdd");

		// Date�� �����ϱ� ���ؼ��� ��¥ ������ yyyy-mm-dd�� �����ؾ� �Ѵ�.
		SimpleDateFormat afterFormat = new SimpleDateFormat("yyyy-mm-dd");

		java.util.Date tempDate = null;

		try {
			// ���� yyyymmdd�ε� ��¥ �������� java.util.Date��ü�� �����.
			tempDate = beforeFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// java.util.Date�� yyyy-mm-dd �������� �����Ͽ� String�� ��ȯ�Ѵ�.
		String transDate = afterFormat.format(tempDate);

		// ��ȯ�� String ���� Date�� �����Ѵ�.
		Date d = Date.valueOf(transDate);

		return d;
	}

	boolean insert(Order o) { // �ֹ����� �߰�
		System.out.println(o);
		//System.out.println("�ֹ����� �߰� �Լ� "+o.getTime());
		// seq_order : ��� ������ �̸�

		// string date -> sql date�� �ٲٱ�
		Date d = transformDate(o.getDate());
		java.sql.Date sqlDate = new java.sql.Date(d.getTime());

		// �ε���, �������̵�, ��, ���ϴ� ��¥, �ð�, �����÷���
		// String sql = "insert into rent_order
		// values(seq_order.nextval,?,?,?,?,default)";
		String sql = "insert into rent_order values(?,?,?,?,?,0)";
		String sql2 = "select seq_order.nextval from dual";
		
		Connection conn = db.getConnect();
		ResultSet rs = null;
		try {
			//PreparedStatement pstmt = conn.prepareStatement(sql);
			/*pstmt.setString(1, o.getId());
			pstmt.setString(2, o.getCarName());
			pstmt.setDate(3, sqlDate);
			pstmt.setString(4, o.getTime());
			
			pstmt.executeUpdate(); // insert ����
			*/
			PreparedStatement pstmt = conn.prepareStatement(sql2);
			rs = pstmt.executeQuery();
			
			int idx=0;
			while (rs.next()) {// rs.next(): �˻� ������� �� �̵�
				idx = rs.getInt(1);
			}
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, idx);
			System.out.println(o.getId());
			pstmt.setString(2, o.getId());
			pstmt.setString(3, o.getCarName());
			pstmt.setDate(4, sqlDate);
			pstmt.setString(5, o.getTime());
			
			pstmt.executeUpdate(); 
			
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	int pay(int orderNum) { // �ֹ� ��ȣ �޾Ƽ� ����
		Connection conn = db.getConnect();

		String searchSql = "select order_flag from rent_order where ORDER_NUM = ?";
		PreparedStatement pstmt;
		ResultSet rs = null;

		boolean chk = true;
		try {
			pstmt = conn.prepareStatement(searchSql);
			pstmt.setInt(1, orderNum);
			rs = pstmt.executeQuery();
			boolean rsChk =rs.next();
			
			if(!rsChk){
				System.out.println("!rs.next()");
				out.println(0);
				chk=false;
			}
			
			else  {// rs.next(): �˻� ������� �� �̵�
				if (rs.getInt(1) == 1) { // �̹� ���� �Ǿ� �ִٸ�
					System.out.println("rs.getint(1) if��");
					out.println(0);
					chk=false;
				} else {
					System.out.println("���������� ���� ���");
					out.println(1);
					searchSql = "select c.price, o.order_time from rent_car c, rent_order o where c.name=o.order_carname and o.order_flag=0 and o.order_num=?";
					pstmt = conn.prepareStatement(searchSql);
					pstmt.setInt(1, orderNum);
					ResultSet rs2 = pstmt.executeQuery();
					
					if (rs2.next()){
						int price = rs2.getInt(1);
						String time = rs2.getString(2);
						System.out.println("���� : "+price);
						if (time.contains(",")){
							String[] t2 = time.split(",");
							out.println(price * (t2.length));
						} else {
							out.println(price);
						}
					
					}
				}
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		if(chk){
		// ������ �� �ִ� ������ ���
			String sql = "update rent_order set order_flag=1 where ORDER_NUM = ?";
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, orderNum);
				pstmt.executeUpdate();
				System.out.println(orderNum + "�� ������ �Ϸ�Ǿ����ϴ�.");
				return 1; // ���� �Ϸ�
	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}

	String EnableTime(Order o) { // ���� ������ �ð��븦 String���� ��ȯ
		String[] time = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "21", "22", "23" };
		String enable_time = "";
		String sql = "select order_time from rent_order where order_carname= ? and order_date = ?";
		Connection conn = db.getConnect();
		PreparedStatement pstmt;
		try {
			boolean exe = false; // ���������� ����Ǿ����� True
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, o.getCarName());
			pstmt.setString(2, o.getDate());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				exe = true;
				System.out.println("rs ����");
				String disable_time = rs.getString(1); // ��� �Ұ��� �ð��� ������
														// (string)
				System.out.println(disable_time);
				if (disable_time.length() > 1) { // 1�ð� �̻� �������
					String[] tmp_time = disable_time.split(",");
					for (String s : tmp_time) {
						time[Integer.parseInt(s)] = null;
					}
				} else { // 1�ð��� ���� ���
					time[Integer.parseInt(disable_time)] = null;
				}
			}


			for (String t : time) {
				if (t != null) {
					System.out.println(t);
					enable_time += ("," + t);
				}
			}

			System.out.println("�ð� : "+ enable_time.substring(1));
			return enable_time.substring(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	ArrayList<Order> payAvailable(User user) {
		System.out.println(user);
		ArrayList<Order> list = new ArrayList<Order>();
		String sql = "select * from rent_order where order_id=? and order_flag=0";
		Connection conn = db.getConnect();
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getId());
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) { // �ֹ��� ������ ������
				java.sql.Date sqlDate = rs.getDate(4);
				String stringDate = sqlDate.toString(); // 2020-05-07
				stringDate = stringDate.replaceAll("-", ""); // 20200507
				list.add(new Order(rs.getInt(1) ,user.getId(), rs.getString(3), stringDate, rs.getString(5), rs.getInt(6)));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("payavail");
		System.out.println(list.size());
		return list;
	}

	ArrayList<Order> printAll(User u) {
		// user�� id�� ã�Ƽ� ����
		Connection conn = db.getConnect();
		String sql = "select * from rent_order where order_id = ?"; // �ش� id��
		// ����� �ֹ���
		// ���� �ޱ�
		ResultSet rs = null;
		ArrayList<Order> list = new ArrayList<Order>();

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, u.getId()); // ������ ���̵� ����
			rs = pstmt.executeQuery();

			// num, id, car name, date, time, flag
			while (rs.next()) {// rs.next(): �˻� ������� �� �̵�
				java.sql.Date sqlDate = rs.getDate(4);
				String stringDate = sqlDate.toString(); // 2020-05-07
				stringDate = stringDate.replaceAll("-", ""); // 20200507

				list.add(new Order(rs.getInt(1), rs.getString(2), rs.getString(3), stringDate, rs.getString(5),
						rs.getInt(6)));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	int cancle(int orderNum) { // �ֹ� ���
		// ���� �̹� ������ �Ǿ� �ִٸ� ����
		Connection conn = db.getConnect();

		String searchSql = "select order_flag from rent_order where ORDER_NUM = ?";
		PreparedStatement pstmt;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(searchSql);
			pstmt.setInt(1, orderNum);
			rs = pstmt.executeQuery();

			while (rs.next()) {// rs.next(): �˻� ������� �� �̵�
				if (rs.getInt(1) == 1) { // �̹� ���� �Ǿ� �ִٸ�
					return 0; // ��� �Ұ�
				}
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// ����� �� �ִ� ������ ���
		String sql = "delete from rent_order where ORDER_NUM = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, orderNum);
			pstmt.executeUpdate();
			System.out.println(orderNum + "�� ������ ��ҵǾ����ϴ�.");
			return 1; // ���� �Ϸ�

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	int IDCheck(String id) { // id�� DB�� ���� �ϴ��� ���� Ȯ��
		String sql = "select * from rent_member where mid=?";
		Connection conn = db.getConnect();
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return 1;
			} else {
				System.out.println("�Է��� id�� �����ϴ�.");
				return 0;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("������ �߻��Ͽ����ϴ�.");
		}
		return 0;
	}

	boolean insert(User u) { // ȸ������
		String sql = "insert into rent_member values(?,?,?,?)";
		Connection conn = db.getConnect();
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, u.getId());
			pstmt.setString(2, u.getPwd());
			pstmt.setInt(3, u.getlNum());
			pstmt.setString(4, u.getName());
			pstmt.executeUpdate();
			conn.close();
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	User login(User user) { // idcheck�� ���� �� ����Ǵ� �α��� �Լ�
		System.out.println("login�Լ� ����");
		String sql = "select license, name from rent_member where mid=? and pwd=?";
		Connection conn = db.getConnect();
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getId());
			pstmt.setString(2, user.getPwd());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) { // ȸ�� ������ �Ǿ� �ִٸ�
				user.setlNum(rs.getInt(1));
				user.setName(rs.getString(2));
				System.out.println("���� : "+user);
				return user;
			} else {
				System.out.println("ȸ�������� �Ǿ����� �ʽ��ϴ�.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
