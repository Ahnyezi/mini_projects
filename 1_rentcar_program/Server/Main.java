package MiniProject2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	public static void main(String[] args) {
		// Ŭ���̾�Ʈ�� ���ϰ� ����
		ServerSocket ss;
		try {
			ss = new ServerSocket(8888);
			while (true) {
				System.out.println("���� ����");
				Socket socket = ss.accept();
				System.out.println("���� ����");
				ServerThread th = new ServerThread(socket);
				th.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
