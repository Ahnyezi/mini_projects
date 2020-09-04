package MiniProject2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	public static void main(String[] args) {
		// 클라이언트의 소켓과 연결
		ServerSocket ss;
		try {
			ss = new ServerSocket(8888);
			while (true) {
				System.out.println("서버 시작");
				Socket socket = ss.accept();
				System.out.println("연결 성공");
				ServerThread th = new ServerThread(socket);
				th.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
