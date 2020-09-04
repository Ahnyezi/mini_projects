package MiniProject2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Service2 {
	private Socket socket;

	private PrintWriter out;
	private BufferedReader br;
	private ObjectInputStream din;
	private ObjectOutputStream oo;

	User user;
	Order order;

	public Service2() {
	}

	public Service2(Socket socket, PrintWriter pw, BufferedReader bbr, ObjectInputStream ois, ObjectOutputStream oos) {
		this.socket = socket;
		out = pw;
		br = bbr;
		din = ois;
		oo = oos;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Order addOrder(Scanner sc) {// 차 이름, 대여 날짜 입력
		System.out.println("<주문>");
		System.out.println("대여하고 싶은 차의 이름를 입력하세요\n종류:suv, tyco, bmw, genesis, ferrari");
		String carName = sc.next();

		// 차 체크
		if (carCheck(carName) == null) {
			System.out.println("잘못된 차 이름을 입력하셨습니다.다시 입력해주세요");
			addOrder(sc);// 물어보기
		}

		System.out.println("대여하고 싶은 날짜를 입력하세요 대여할 날짜를 입력하세요 ex.대여희망날짜:2020년 5월 8일=>20200508");
		String date = sc.next();

		// int num, String id, String carName, String date, String time, int
		// flag
		order = new Order(55, user.getId(), carName, date, "", 0);
		return order;
	}

	public String addTime(Scanner sc, String time) {// 대여 시간 선택 **검토
		boolean flag = true;
		int start = 0, end = 0;
		do {
			System.out.println("<대여시간선택>");//
			System.out.println("현재 대여가능시간:" + time);//
			System.out.println("ex.대여희망시간:오전10 ~ 오후4시=> 시작시간:10, 반납시간:15");
			System.out.println("대여 시작 시간을 입력하세요 (숫자만 입력)");
			start = sc.nextInt();
			System.out.println("차량 반납 시간을 입력하세요 (숫자만 입력)");
			end = sc.nextInt();
		} while (start < 0 || end > 23 || start >= end);

		String time2 = "";
		time2 += start;
		for (int i = start + 1; i < end; i++) {// 대여시간 string 생성
			time2 += "," + i;
		}
		return time2;// 반환
	}

	// 차 체크
	public String carCheck(String carName) {
		boolean flag;
		switch (carName) {
		case "suv":
			flag = true;
			break;
		case "tyco":
			flag = true;
			break;
		case "genesis":
			flag = true;
			break;
		case "ferrari":
			flag = true;
			break;
		case "bmw":
			flag = true;
			break;
		default:
			flag = false;
		}
		if (flag == true) {
			return carName;
		}
		return null;
	}

	public void orderCheck(Scanner sc, String time) {// server에서 반환된 주문 확인
		if (time == null) {
			System.out.println("잘못된 주문을 입력하였습니다. 다시 입력해주세요.");
			addOrder(sc);
		}
	}

	public boolean payCheck(Scanner sc) {// 바로결제여부 확인
		System.out.println("결제창으로 넘어가시겠습니까?(번호를 입력해주세요)");
		System.out.println("1.YES 2.NO");
		int op = sc.nextInt();
		while (true) {
			switch (op) {
			case 1:// 바로 결제=>pay
				return true;
			case 2:// 나중에 결제
				return false;
			default:
				System.out.println("잘못된 번호를 입력하셨습니다.");
			}
		}
	}

	public void printAvailable() {// 미결제 목록 출력
		ArrayList<Order> list;
		Iterator<Order> iterator;

		System.out.println("<미결제 목록 출력>");
		try {
			out.println("/PayAvailable");//
			oo.writeObject(user);
			// System.out.println(user);

			list = (ArrayList<Order>) din.readObject();
			// System.out.println(list.size());

			iterator = list.iterator();
			while (iterator.hasNext()) {
				order = (Order) iterator.next();
				System.out.println(order);
			}

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void PrintAll() {// 전체 주문목록 출력
		ArrayList<Order> list;
		Iterator<Order> iterator;
		System.out.println("<주문 목록 출력>");
		try {
			out.println("/PrintAll");
			oo.writeObject(user);

			list = (ArrayList<Order>) din.readObject();
			iterator = list.iterator();
			while (iterator.hasNext()) {
				order = (Order) iterator.next();
				System.out.println(order);
			} // while
		} // try
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int selectPay(Scanner sc, int orderNum) {// 메뉴에서 결제 선택한 경우
		printAvailable();
		System.out.println("결제할 주문번호를 선택하세요");
		orderNum = sc.nextInt();
		return orderNum;
	}

	public boolean order(Scanner sc) {// 주문 전체 메서드
		String time;
		boolean flag = true;
		order = addOrder(sc);// 1. 주문정보 입력받기

		try {
			// 2. 서버로 객체 보내기
			out.println("/order");
			oo.writeObject(order);

			// 3. 서버에서 String(time) 받기
			/*
			 * while ((time = br.readLine()) != null) { } // **검토
			 */

			time = br.readLine();// 가능시간
			orderCheck(sc, time);// 3. 반환된 String 확인(주문 올바른지 확인)

			//System.out.println(time);
			time = addTime(sc, time);// 4. 대여시간 입력받기
			order.setTime(time);
			//System.out.println(order.getTime());

			// int num, String id, String carName, String date, String time, int
			// flag

			Order neworder = new Order(45, order.getId(), order.getCarName(), order.getDate(), time, 0);

			//System.out.println(neworder);
			oo.writeObject(neworder);// 5. 완성된 객체 보내기
			//System.out.println(order);

			String result = br.readLine();
			if (result.equals("true")) {
				System.out.println("주문이 완료되었습니다.");
			} else {
				System.out.println("주문이 실패했습니다.");
			}

			// 4. 바로결제 여부
			flag = payCheck(sc);
			return flag;

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	public boolean pay(Scanner sc, int orderNum) {
		if (orderNum == -1) {// 메뉴에서 선택해서 온 경우
			orderNum = selectPay(sc, -1);
		}
		try {
			out.println("/pay");
			out.println(orderNum);// 해당 아이디로 주문할 orderNum 보내기

			String flag = br.readLine(); // 결제가 되어있으면 0, 결제가 되어있지않으면 1
			// System.out.println(flag); //0 or 1

			if (flag.equals("1")) { // 결제가능{
				String tmp = br.readLine();
				// System.out.println(tmp);
				int price = Integer.parseInt(tmp); // 가격
				System.out.println("결제하실 금액은 " + price + "입니다.");
			}

			String result = br.readLine(); // 결과값
			// System.out.println(result);

			if (result.equals("1")) { // 결제까지 완료
				order.setFlag(1);
				System.out.println("결제가 완료되었습니다.");
			} else {
				System.out.println("결제실패"); // 이미 결제되어있는경우, 서버 터져 update
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void cancelOrder(Scanner sc) {// 주문취소
		printAvailable();
		try {
			System.out.println("취소 희망하는 주문 번호 입력하시오(숫자만 입력):");
			out.println("/cancle");
			out.println(sc.nextInt());

			String flag = br.readLine();// 복x:false, 중복o:true

			if (flag.equals("1")) {
				System.out.println("취소 완료");
			} else {
				System.out.println("취소 실패");
			} // else
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch
	}

}
