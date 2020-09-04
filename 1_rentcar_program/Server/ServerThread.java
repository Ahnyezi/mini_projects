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
					System.out.println("클라이언트 종료");
					break;
				} else if (msg.contains("idCheck")){
					System.out.println("아이디 체크");
					out.println(IDCheck(br.readLine()));
				} else if (msg.contains("/insert")){
					System.out.println("회원가입");
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
					System.out.println("주문");
					try {
						String time = EnableTime((Order)os.readObject());
						out.println(time);
						out.println(insert((Order)os.readObject()));
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.contains("/pay")){
					System.out.println("결제");
					String num = br.readLine();

					out.println(pay(Integer.parseInt(num)));
				} else if (msg.contains("/PayAvailable")){
					System.out.println("결제 가능 목록 출력");
					ArrayList<Order> al;
					try {
						al = payAvailable((User)os.readObject());
						oo.writeObject(al);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.contains("/PrintAll")){	//ArrayList<Order> printAll(User u)
					System.out.println("전체 출력");
					ArrayList<Order> al;
					try {
						al = printAll((User)os.readObject());
						oo.writeObject(al);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.contains("/cancle")){	// boolean cancle(int orderNum)
					System.out.println("취소");
					out.println(cancle(Integer.parseInt(br.readLine())));
				} 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} // 클라이언트와 정보 교환 (메시지를 받아옴)

	void createSeq() {
		System.out.println("시작");
		Connection conn = db.getConnect();
		String sql = "select * from USER_SEQUENCES where 'SEQ_ORDER' in (SELECT sequence_name FROM USER_SEQUENCES)";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println("seq_order 시퀀스 이미 존재");
				return;
			}
			String sql2 = "create sequence seq_order nocache";
			pstmt = conn.prepareStatement(sql2);
			pstmt.executeUpdate();
			System.out.println("seq_order 시퀀스 생성 성공");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("seq_order 시퀀스 생성 실패");
			e.printStackTrace();
		}
	}

	public Date transformDate(String date) // string 형식을 date 형식으로 바꾸기
	{
		SimpleDateFormat beforeFormat = new SimpleDateFormat("yyyymmdd");

		// Date로 변경하기 위해서는 날짜 형식을 yyyy-mm-dd로 변경해야 한다.
		SimpleDateFormat afterFormat = new SimpleDateFormat("yyyy-mm-dd");

		java.util.Date tempDate = null;

		try {
			// 현재 yyyymmdd로된 날짜 형식으로 java.util.Date객체를 만든다.
			tempDate = beforeFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// java.util.Date를 yyyy-mm-dd 형식으로 변경하여 String로 반환한다.
		String transDate = afterFormat.format(tempDate);

		// 반환된 String 값을 Date로 변경한다.
		Date d = Date.valueOf(transDate);

		return d;
	}

	boolean insert(Order o) { // 주문내역 추가
		System.out.println(o);
		//System.out.println("주문내역 추가 함수 "+o.getTime());
		// seq_order : 사용 시퀀스 이름

		// string date -> sql date로 바꾸기
		Date d = transformDate(o.getDate());
		java.sql.Date sqlDate = new java.sql.Date(d.getTime());

		// 인덱스, 유저아이디, 차, 원하는 날짜, 시간, 결제플래그
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
			
			pstmt.executeUpdate(); // insert 실행
			*/
			PreparedStatement pstmt = conn.prepareStatement(sql2);
			rs = pstmt.executeQuery();
			
			int idx=0;
			while (rs.next()) {// rs.next(): 검색 결과에서 줄 이동
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

	int pay(int orderNum) { // 주문 번호 받아서 결제
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
			
			else  {// rs.next(): 검색 결과에서 줄 이동
				if (rs.getInt(1) == 1) { // 이미 결제 되어 있다면
					System.out.println("rs.getint(1) if문");
					out.println(0);
					chk=false;
				} else {
					System.out.println("결제가능함 가격 출력");
					out.println(1);
					searchSql = "select c.price, o.order_time from rent_car c, rent_order o where c.name=o.order_carname and o.order_flag=0 and o.order_num=?";
					pstmt = conn.prepareStatement(searchSql);
					pstmt.setInt(1, orderNum);
					ResultSet rs2 = pstmt.executeQuery();
					
					if (rs2.next()){
						int price = rs2.getInt(1);
						String time = rs2.getString(2);
						System.out.println("가격 : "+price);
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
		// 결제할 수 있는 예약일 경우
			String sql = "update rent_order set order_flag=1 where ORDER_NUM = ?";
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, orderNum);
				pstmt.executeUpdate();
				System.out.println(orderNum + "의 결제가 완료되었습니다.");
				return 1; // 결제 완료
	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}

	String EnableTime(Order o) { // 예약 가능한 시간대를 String으로 반환
		String[] time = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "21", "22", "23" };
		String enable_time = "";
		String sql = "select order_time from rent_order where order_carname= ? and order_date = ?";
		Connection conn = db.getConnect();
		PreparedStatement pstmt;
		try {
			boolean exe = false; // 정상적으로 수행되었으면 True
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, o.getCarName());
			pstmt.setString(2, o.getDate());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				exe = true;
				System.out.println("rs 있음");
				String disable_time = rs.getString(1); // 사용 불가한 시간을 가져옴
														// (string)
				System.out.println(disable_time);
				if (disable_time.length() > 1) { // 1시간 이상 빌릴경우
					String[] tmp_time = disable_time.split(",");
					for (String s : tmp_time) {
						time[Integer.parseInt(s)] = null;
					}
				} else { // 1시간만 빌린 경우
					time[Integer.parseInt(disable_time)] = null;
				}
			}


			for (String t : time) {
				if (t != null) {
					System.out.println(t);
					enable_time += ("," + t);
				}
			}

			System.out.println("시간 : "+ enable_time.substring(1));
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
			
			while (rs.next()) { // 주문한 내역이 있으면
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
		// user의 id로 찾아서 결제
		Connection conn = db.getConnect();
		String sql = "select * from rent_order where order_id = ?"; // 해당 id의
		// 사람이 주문한
		// 내역 받기
		ResultSet rs = null;
		ArrayList<Order> list = new ArrayList<Order>();

		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, u.getId()); // 유저의 아이디 삽입
			rs = pstmt.executeQuery();

			// num, id, car name, date, time, flag
			while (rs.next()) {// rs.next(): 검색 결과에서 줄 이동
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

	int cancle(int orderNum) { // 주문 취소
		// 만약 이미 결제가 되어 있다면 실패
		Connection conn = db.getConnect();

		String searchSql = "select order_flag from rent_order where ORDER_NUM = ?";
		PreparedStatement pstmt;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(searchSql);
			pstmt.setInt(1, orderNum);
			rs = pstmt.executeQuery();

			while (rs.next()) {// rs.next(): 검색 결과에서 줄 이동
				if (rs.getInt(1) == 1) { // 이미 결제 되어 있다면
					return 0; // 취소 불가
				}
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 취소할 수 있는 예약일 경우
		String sql = "delete from rent_order where ORDER_NUM = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, orderNum);
			pstmt.executeUpdate();
			System.out.println(orderNum + "의 예약이 취소되었습니다.");
			return 1; // 결제 완료

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	int IDCheck(String id) { // id가 DB에 존재 하는지 여부 확인
		String sql = "select * from rent_member where mid=?";
		Connection conn = db.getConnect();
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return 1;
			} else {
				System.out.println("입력한 id가 없습니다.");
				return 0;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("오류가 발생하였습니다.");
		}
		return 0;
	}

	boolean insert(User u) { // 회원가입
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

	User login(User user) { // idcheck가 끝난 뒤 실행되는 로그인 함수
		System.out.println("login함수 실행");
		String sql = "select license, name from rent_member where mid=? and pwd=?";
		Connection conn = db.getConnect();
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getId());
			pstmt.setString(2, user.getPwd());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) { // 회원 가입이 되어 있다면
				user.setlNum(rs.getInt(1));
				user.setName(rs.getString(2));
				System.out.println("전송 : "+user);
				return user;
			} else {
				System.out.println("회원가입이 되어있지 않습니다.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
