package MiniProject2;

import java.io.Serializable;

public class User implements Serializable {
	private String id;//아이디
	private String pwd;//비밀번호
	private String name;//이름
	private int lNum;//면허번호

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

	 //user 클래스
	   @Override
	   public String toString() {
	      return "사용자 정보\n [ID:" + id + ", PWD:" + pwd + ", 이름:" + name + ", 면허번호:" + lNum + "]";
	   }
	
}
