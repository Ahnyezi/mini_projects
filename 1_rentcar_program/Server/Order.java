package MiniProject2;

import java.io.Serializable;

public class Order implements Serializable {
   private int num; // index
   private String id;
   private String carName;
   private String date;
   private String time; // 1,2,3,4,5 (1~5시 까지 예약)
   private int flag; // 결제 되었는지 아닌지 판단(1- 결제 완료, 0-결제 안함)

   public Order() {
   }

   public Order(String id, String carName, String date, String time) {
      this.id = id;
      this.carName = carName;
      this.date = date;
      this.time = time;
   }
   
   public Order(int num, String id, String carName, String date, String time, int flag) {
      this.num = num;
      this.id = id;
      this.carName = carName;
      this.date = date;
      this.time = time;
      this.flag = flag;
   }

   
   public int getNum() {
      return num;
   }

   public void setNum(int num) {
      this.num = num;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getCarName() {
      return carName;
   }

   public void setCarName(String carName) {
      this.carName = carName;
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public String getTime() {
      return time;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public int getFlag() {
      return flag;
   }

   public void setFlag(int flag) {
      this.flag = flag;
   }

   @Override
   public String toString() {
      return "Order [num=" + num + ", id=" + id + ", carName=" + carName + ", date=" + date + ", time=" + time
            + ", flag=" + flag + "]";
   }

}