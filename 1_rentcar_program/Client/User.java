package MiniProject2;

import java.io.Serializable;

public class User implements Serializable {
	private String id;//���̵�
	private String pwd;//��й�ȣ
	private String name;//�̸�
	private int lNum;//�����ȣ

	public User() {
	}
	
	public User(String id, String pwd, String name, int lNum) {
		this.id = id;
		this.pwd = pwd;
		this.name = name;
		this.lNum = lNum;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getlNum() {
		return lNum;
	}
	public void setlNum(int lNum) {
		this.lNum = lNum;
	}

	 //user Ŭ����
	   @Override
	   public String toString() {
	      return "����� ����\n [ID:" + id + ", PWD:" + pwd + ", �̸�:" + name + ", �����ȣ:" + lNum + "]";
	   }
	
}
