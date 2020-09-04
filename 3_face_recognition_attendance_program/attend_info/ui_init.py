import cv2
import tkinter as tk
from PIL import Image
from PIL import ImageTk


class AppWindow(tk.Frame):
    """창 초기화"""

    def __init__(self, master=None, path=None):
        super().__init__(master)
        self.master = master
        self.master.title('Attendance Information')
        self.master.geometry('600x300+100+100')
        self.master.resizable(False, False)
        self.pack()
        self.sub_fr = None  # 위젯 배치용
        # 로고 배치용
        self.src = None
        self.logo = None
        self.create_widgets(path)  # 위젯 생성 및 배치

    def make_img(self, path):
        """로고 이미지 읽어오기"""
        src = cv2.imread(path)
        img = cv2.cvtColor(src, cv2.COLOR_BGR2RGB)
        img = Image.fromarray(img)
        self.src = ImageTk.PhotoImage(image=img)

    def create_widgets(self, path):
        """로고 이미지 띄우기 + 위젯 담는 서브 프레임"""
        self.make_img(path)
        self.logo = tk.Label(self.master, image=self.src)
        self.logo.pack()
        self.sub_fr = tk.Frame(self.master)
        self.sub_fr.pack()
