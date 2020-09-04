package MiniProject2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Service1 {
	private Socket socket;

	private PrintWriter out;
	private BufferedReader br;
	private ObjectInputStream din;
	private ObjectOutputStream oo;
	
	private User user;
	
	public Service1() {}

	public Service1 (Socket socket, PrintWriter pw, BufferedReader bbr, ObjectInputStream ois, ObjectOutputStream oos) {
		// String id, String pwd, String name, int lNum
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

	public void join(Scanner sc) {
		boolean flag = true;

		while (flag) {
			System.out.println("<ȸ������>");
			System.out.println("ID:");
			String id = sc.next();
			try {
				out.println("/////idCheck");
				out.println(id); // ���̵� �ߺ�Ȯ��

				String idCheck = br.readLine();// ��x:false, �ߺ�o:true

				if (idCheck.equals("0")) {// ���̵� ���� ���ϸ� 0
					System.out.println("PWD:"); String pwd = sc.next();
					System.out.println("�̸�:"); String name = sc.next();
					System.out.println("�����ȣ: "); int lNum = sc.nextInt();

					out.println("/insert");
					User u = new User(id, pwd, name, lNum);
					oo.writeObject(u);// ��ü ������
					
					String result= br.readLine();
					if(result.equals("true")){
						System.out.println("ȸ������ ����");
					}
					else{
						System.out.println("ȸ�������� �����߽��ϴ�.");
					}
					
					flag = false;
				} else {// ���̵� �ߺ�
					System.out.println("�̹� ��� ���� ���̵� �Դϴ�. �ٽ� �Է����ּ���.");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public User logIn(Scanner sc) {
		System.out.println("<�α���>");
		System.out.println("ID:"); String id = sc.next();
		System.out.println("PWD:"); String pwd = sc.next();

		try {
			user = new User(id, pwd, "", 1); // ���� ����
			
			out.println("/login");
			//System.out.println(user);
			
			oo.writeObject(user);// ��ü ������
			oo.flush();
			
			user = (User) din.readObject();
			if (user == null) {
				System.out.println("���̵�/��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
			} else {
				System.out.println("�α��� ����");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
}