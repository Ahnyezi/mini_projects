package MiniProject2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Menu {
	Socket socket;
	private PrintWriter out;
	private BufferedReader br;
	private ObjectInputStream din;
	private ObjectOutputStream oo;

	Service1 service1;
	Service2 service2;
	User user;
	
	public Menu() {
	}

	public Menu(Socket socket) {
		this.socket = socket;
		try {
			oo = new ObjectOutputStream(socket.getOutputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			din = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// (Socket socket, PrintWriter pw, BufferedReader bbr, ObjectInputStream
		// ois, ObjectOutputStream oos) {
		service1 = new Service1(socket, out, br, din, oo);
		service2 = new Service2(socket, out, br, din, oo);

	}

	public void run1(Scanner sc) {
		String str = "<메뉴>\n1.회원가입 2.로그인 3.종료";
		int menu = 0;
		boolean flag = true;

		while (flag) {
			System.out.println(str);
			menu = sc.nextInt();
			switch (menu) {
			case 1:
				service1.join(sc);
				break;
			case 2:
				user = service1.logIn(sc); // user 대입
				if(user!=null) {
					run2(sc);
					flag = false;
				}
				break;
			case 3:
				flag = false;
				break;
			default:
				System.out.println("메뉴를 잘못 입력하셨습니다.");
			}
		}
	}
	
	public void run2(Scanner sc){
		String str = "<메뉴>\n1.주문하기 2.결제  3.주문목록출력  4.주문취소 5.종료";
		int menu = 0;
		boolean flag = true;
		service2.setUser(user); 
		
		while(flag){
			System.out.println(str);
			menu=sc.nextInt();
			switch(menu){
			case 1:
				boolean result = service2.order(sc);
				if(result==true){
					service2.pay(sc, -1);
				}
				break;
			case 2:
				service2.pay(sc, -1);
				break;
			case 3:
				service2.PrintAll();
				break;
			case 4:
				service2.cancelOrder(sc);
				break;
			case 5:
				flag=false;
				break;
			default:
				System.out.println("메뉴를 잘못 입력하셨습니다. 다시 입력해주세요.");
			}
		}

	}
}
