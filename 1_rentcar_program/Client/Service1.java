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
			System.out.println("<회원가입>");
			System.out.println("ID:");
			String id = sc.next();
			try {
				out.println("/////idCheck");
				out.println(id); // 아이디 중복확인

				String idCheck = br.readLine();// 복x:false, 중복o:true

				if (idCheck.equals("0")) {// 아이디 존재 안하면 0
					System.out.println("PWD:"); String pwd = sc.next();
					System.out.println("이름:"); String name = sc.next();
					System.out.println("면허번호: "); int lNum = sc.nextInt();

					out.println("/insert");
					User u = new User(id, pwd, name, lNum);
					oo.writeObject(u);// 객체 보내기
					
					String result= br.readLine();
					if(result.equals("true")){
						System.out.println("회원가입 성공");
					}
					else{
						System.out.println("회원가입이 실패했습니다.");
					}
					
					flag = false;
				} else {// 아이디 중복
					System.out.println("이미 사용 중인 아이디 입니다. 다시 입력해주세요.");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public User logIn(Scanner sc) {
		System.out.println("<로그인>");
		System.out.println("ID:"); String id = sc.next();
		System.out.println("PWD:"); String pwd = sc.next();

		try {
			user = new User(id, pwd, "", 1); // 유저 생성
			
			out.println("/login");
			//System.out.println(user);
			
			oo.writeObject(user);// 객체 보내기
			oo.flush();
			
			user = (User) din.readObject();
			if (user == null) {
				System.out.println("아이디/비밀번호가 일치하지 않습니다.");
			} else {
				System.out.println("로그인 성공");
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