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

	public Order addOrder(Scanner sc) {// �� �̸�, �뿩 ��¥ �Է�
		System.out.println("<�ֹ�>");
		System.out.println("�뿩�ϰ� ���� ���� �̸��� �Է��ϼ���\n����:suv, tyco, bmw, genesis, ferrari");
		String carName = sc.next();

		// �� üũ
		if (carCheck(carName) == null) {
			System.out.println("�߸��� �� �̸��� �Է��ϼ̽��ϴ�.�ٽ� �Է����ּ���");
			addOrder(sc);// �����
		}

		System.out.println("�뿩�ϰ� ���� ��¥�� �Է��ϼ��� �뿩�� ��¥�� �Է��ϼ��� ex.�뿩�����¥:2020�� 5�� 8��=>20200508");
		String date = sc.next();

		// int num, String id, String carName, String date, String time, int
		// flag
		order = new Order(55, user.getId(), carName, date, "", 0);
		return order;
	}

	public String addTime(Scanner sc, String time) {// �뿩 �ð� ���� **����
		boolean flag = true;
		int start = 0, end = 0;
		do {
			System.out.println("<�뿩�ð�����>");//
			System.out.println("���� �뿩���ɽð�:" + time);//
			System.out.println("ex.�뿩����ð�:����10 ~ ����4��=> ���۽ð�:10, �ݳ��ð�:15");
			System.out.println("�뿩 ���� �ð��� �Է��ϼ��� (���ڸ� �Է�)");
			start = sc.nextInt();
			System.out.println("���� �ݳ� �ð��� �Է��ϼ��� (���ڸ� �Է�)");
			end = sc.nextInt();
		} while (start < 0 || end > 23 || start >= end);

		String time2 = "";
		time2 += start;
		for (int i = start + 1; i < end; i++) {// �뿩�ð� string ����
			time2 += "," + i;
		}
		return time2;// ��ȯ
	}

	// �� üũ
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

	public void orderCheck(Scanner sc, String time) {// server���� ��ȯ�� �ֹ� Ȯ��
		if (time == null) {
			System.out.println("�߸��� �ֹ��� �Է��Ͽ����ϴ�. �ٽ� �Է����ּ���.");
			addOrder(sc);
		}
	}

	public boolean payCheck(Scanner sc) {// �ٷΰ������� Ȯ��
		System.out.println("����â���� �Ѿ�ðڽ��ϱ�?(��ȣ�� �Է����ּ���)");
		System.out.println("1.YES 2.NO");
		int op = sc.nextInt();
		while (true) {
			switch (op) {
			case 1:// �ٷ� ����=>pay
				return true;
			case 2:// ���߿� ����
				return false;
			default:
				System.out.println("�߸��� ��ȣ�� �Է��ϼ̽��ϴ�.");
			}
		}
	}

	public void printAvailable() {// �̰��� ��� ���
		ArrayList<Order> list;
		Iterator<Order> iterator;

		System.out.println("<�̰��� ��� ���>");
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

	public void PrintAll() {// ��ü �ֹ���� ���
		ArrayList<Order> list;
		Iterator<Order> iterator;
		System.out.println("<�ֹ� ��� ���>");
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

	public int selectPay(Scanner sc, int orderNum) {// �޴����� ���� ������ ���
		printAvailable();
		System.out.println("������ �ֹ���ȣ�� �����ϼ���");
		orderNum = sc.nextInt();
		return orderNum;
	}

	public boolean order(Scanner sc) {// �ֹ� ��ü �޼���
		String time;
		boolean flag = true;
		order = addOrder(sc);// 1. �ֹ����� �Է¹ޱ�

		try {
			// 2. ������ ��ü ������
			out.println("/order");
			oo.writeObject(order);

			// 3. �������� String(time) �ޱ�
			/*
			 * while ((time = br.readLine()) != null) { } // **����
			 */

			time = br.readLine();// ���ɽð�
			orderCheck(sc, time);// 3. ��ȯ�� String Ȯ��(�ֹ� �ùٸ��� Ȯ��)

			//System.out.println(time);
			time = addTime(sc, time);// 4. �뿩�ð� �Է¹ޱ�
			order.setTime(time);
			//System.out.println(order.getTime());

			// int num, String id, String carName, String date, String time, int
			// flag

			Order neworder = new Order(45, order.getId(), order.getCarName(), order.getDate(), time, 0);

			//System.out.println(neworder);
			oo.writeObject(neworder);// 5. �ϼ��� ��ü ������
			//System.out.println(order);

			String result = br.readLine();
			if (result.equals("true")) {
				System.out.println("�ֹ��� �Ϸ�Ǿ����ϴ�.");
			} else {
				System.out.println("�ֹ��� �����߽��ϴ�.");
			}

			// 4. �ٷΰ��� ����
			flag = payCheck(sc);
			return flag;

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	public boolean pay(Scanner sc, int orderNum) {
		if (orderNum == -1) {// �޴����� �����ؼ� �� ���
			orderNum = selectPay(sc, -1);
		}
		try {
			out.println("/pay");
			out.println(orderNum);// �ش� ���̵�� �ֹ��� orderNum ������

			String flag = br.readLine(); // ������ �Ǿ������� 0, ������ �Ǿ����������� 1
			// System.out.println(flag); //0 or 1

			if (flag.equals("1")) { // ��������{
				String tmp = br.readLine();
				// System.out.println(tmp);
				int price = Integer.parseInt(tmp); // ����
				System.out.println("�����Ͻ� �ݾ��� " + price + "�Դϴ�.");
			}

			String result = br.readLine(); // �����
			// System.out.println(result);

			if (result.equals("1")) { // �������� �Ϸ�
				order.setFlag(1);
				System.out.println("������ �Ϸ�Ǿ����ϴ�.");
			} else {
				System.out.println("��������"); // �̹� �����Ǿ��ִ°��, ���� ���� update
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void cancelOrder(Scanner sc) {// �ֹ����
		printAvailable();
		try {
			System.out.println("��� ����ϴ� �ֹ� ��ȣ �Է��Ͻÿ�(���ڸ� �Է�):");
			out.println("/cancle");
			out.println(sc.nextInt());

			String flag = br.readLine();// ��x:false, �ߺ�o:true

			if (flag.equals("1")) {
				System.out.println("��� �Ϸ�");
			} else {
				System.out.println("��� ����");
			} // else
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch
	}

}
