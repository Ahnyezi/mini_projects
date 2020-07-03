import cv2, os, threading
import tkinter as tk
from PIL import ImageTk, Image

f_list = None
frame = None

class SampleApp(tk.Tk):
   def __init__(self):
       tk.Tk.__init__(self)
       self._frame = None
       self.switch_frame(StartPage)

   def switch_frame(self, frame_class):
       new_frame = frame_class(self)
       if self._frame is not None:
           self._frame.destroy()
       self._frame = new_frame
       self._frame.pack()

class StartPage(tk.Frame):
   def __init__(self, master):
       tk.Frame.__init__(self, master)
       tk.Label(self, text="My Cam", font=('Helvetica', 18, "bold")).pack(side="top", fill="x", pady=5)
       tk.Button(self, text="사진 촬영",
                 command=lambda: master.switch_frame(PageOne)).pack()
       tk.Button(self, text="갤러리",
                 command=lambda: master.switch_frame(PageTwo)).pack()
class PageOne(tk.Frame):
   def __init__(self, master):
       self.frame = None
       self.ret = None
       self.f_list = os.listdir("galary")
       self.cnt = len(self.f_list)
       tk.Frame.__init__(self, master)
       tk.Frame.configure(self,bg='blue')
       tk.Label(self, text="모드 선택", font=('Helvetica', 13, "bold")).pack(side="top", fill="x", pady=5)
       tk.Button(self, text="Gray",
                 command=lambda:self.mode1()).pack()
       tk.Button(self, text="Color",
                 command=lambda:self.mode2()).pack()
       tk.Button(self, text="촬영",
                 command=lambda:self.shoot()).pack()
       tk.Label(self, text="c를 입력하여 화면을 멈춘 뒤, 촬영버튼을 누르세요.", font=('Helvetica', 10, "bold")).pack(side="top", fill="x", pady=5)
       tk.Button(self, text="Go back to start page",
                 command=lambda: master.switch_frame(StartPage)).pack()

   def mode1(self):
       print("<mode1>")
       cap = cv2.VideoCapture(0)
       print('width:{0},height:{1}'.format(cap.get(3), cap.get(4)))
       cap.set(3, 400)
       cap.set(4, 300)
       while True:
           self.ret, self.frame = cap.read()
           if self.ret:
               self.frame = cv2.cvtColor(self.frame, cv2.cv2.COLOR_BGR2GRAY)
               cv2.imshow('myCam', self.frame)
               if cv2.waitKey(1) & 0xFF == ord('c'):  # c키를 누르면 화면 멈춤
                   break

   def mode2(self):
       print("<mode2>")
       cap = cv2.VideoCapture(0)
       print('width:{0},height:{1}'.format(cap.get(3), cap.get(4)))
       cap.set(3, 400)
       cap.set(4, 300)
       while True:
           self.ret, self.frame = cap.read()
           if self.ret:
               cv2.imshow('myCam', self.frame)
               if cv2.waitKey(1) & 0xFF == ord('c'):  # c키를 누르면 화면 멈춤
                   break

   def shoot(self):
       if self.ret:
           print("찰칵")
           cv2.imwrite("galary/" + str(self.cnt) + ".png", self.frame)
           cv2.imshow(str(self.cnt - 1) + '.png', self.frame)
           k = cv2.waitKey(0)
           if k == 27:  # esc key
               cv2.destroyAllWindow()

class PageTwo(tk.Frame):
   f_name = 0
   def __init__(self, master):
       self.f_list = os.listdir("galary")
       self.f_names = ''
       for idx,num in enumerate(self.f_list):
           self.f_names += str(idx)+") "+num+"\n"
       tk.Frame.__init__(self, master)
       tk.Frame.configure(self,bg='blue')
       tk.Label(self, text="갤러리", font=('Helvetica', 13, "bold")).pack(side="top", fill="x", pady=5)
       # 이미지 출력
       self.photo = ImageTk.PhotoImage(Image.open("galary/"+str(PageTwo.f_name)+".png")) #pil solution
       self.img=tk.Label(self, image=self.photo)
       self.img.pack()
       self.img_name=tk.Label(self, text=str(PageTwo.f_name)+".png", font=('Helvetica', 13, "bold"))
       self.img_name.pack()
       tk.Button(self, text="prev",
                 command=lambda: self.prev_img()).pack()
       tk.Button(self, text="next",
                 command=lambda: self.next_img()).pack()
       tk.Button(self, text="Go back to start page",
                 command=lambda: master.switch_frame(StartPage)).pack()

   def prev_img(self):
       if(PageTwo.f_name >0):
           PageTwo.f_name-=1
           self.photo = ImageTk.PhotoImage(Image.open("galary/"+str(PageTwo.f_name)+".png"))
           self.img.config(image=self.photo)
           self.img_name.config(text=str(PageTwo.f_name)+".png")
           print(str(PageTwo.f_name)+".png 로 전환")
       else:
           print("이전 사진이 없습니다.")

   def next_img(self):
       if(PageTwo.f_name < len(self.f_list)-1):
           PageTwo.f_name+=1
           self.photo = ImageTk.PhotoImage(Image.open("galary/"+str(PageTwo.f_name)+".png")) #pil solution
           self.img.config(image=self.photo)
           self.img_name.config(text=str(PageTwo.f_name)+".png")
           print(str(PageTwo.f_name)+".png 로 전환")
       else:
           print("다음 사진이 없습니다.")

app = SampleApp()
app.mainloop()
