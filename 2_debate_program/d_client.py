# -*- coding: utf-8 -*-
# Form implementation generated from reading ui file 'client.ui'
#
# Created by: PyQt5 UI code generator 5.13.0
#
# WARNING! All changes made in this file will be lost!
import os
import time
import socket, threading
from PyQt5.QtCore import *
from PyQt5.QtWebKitWidgets import *
from PyQt5.QtWidgets import QMessageBox
from PyQt5 import QtCore, QtGui, QtWidgets
import sys
myip=socket.gethostbyname(socket.getfqdn())
HOST = '192.168.22.128'
PORT = 9999
flag = False
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect((HOST, PORT))
#--------------------------------전체적인 UI구성------------------------------
class Ui_mainWindow(object):
  def setupUi(self, mainWindow):
    mainWindow.setObjectName("mainWindow")
    mainWindow.resize(1116, 807)
    self.centralwidget = QtWidgets.QWidget(mainWindow)
    self.centralwidget.setObjectName("centralwidget")
    self.gridLayout = QtWidgets.QGridLayout(self.centralwidget)
    self.gridLayout.setObjectName("gridLayout")
    self.pushButton_3 = QtWidgets.QPushButton(self.centralwidget)
    self.pushButton_3.setObjectName("text_push_btn")
    self.gridLayout.addWidget(self.pushButton_3, 2, 1, 1, 1)
    self.lineEdit = QtWidgets.QLineEdit(self.centralwidget)
    self.lineEdit.setObjectName("lineEdit")
    self.gridLayout.addWidget(self.lineEdit, 2, 0, 1, 1)
    self.verticalLayout = QtWidgets.QVBoxLayout()
    self.verticalLayout.setObjectName("verticalLayout")
    self.logWindow = QtWidgets.QTextBrowser(self.centralwidget)
    self.logWindow.setObjectName("logWindow")
    self.logWindowWidgetContents = QtWidgets.QWidget()
    self.logWindowWidgetContents.setGeometry(QtCore.QRect(0, 0, 501, 105))
    self.logWindowWidgetContents.setObjectName("logWindowWidgetContents")
    self.verticalLayout.addWidget(self.logWindow)
    self.gridLayout.addLayout(self.verticalLayout, 1, 2, 1, 1)
    self.horizontalLayout_2 = QtWidgets.QHBoxLayout()
    self.horizontalLayout_2.setObjectName("horizontalLayout_2")
    self.chatWindow = QtWidgets.QTextBrowser(self.centralwidget)
    self.chatWindow.setObjectName("chatWindow")
    self.chatWindowWidgetContents = QtWidgets.QWidget()
    self.chatWindowWidgetContents.setGeometry(QtCore.QRect(0, 0, 583, 105))
    self.chatWindowWidgetContents.setObjectName("chatWindowWidgetContents")
    self.horizontalLayout_2.addWidget(self.chatWindow)
    self.gridLayout.addLayout(self.horizontalLayout_2, 1, 0, 1, 2)
    self.horizontalLayout = QtWidgets.QHBoxLayout()
    self.horizontalLayout.setObjectName("horizontalLayout")
    self.pushButton_4 = QtWidgets.QPushButton(self.centralwidget)
    self.pushButton_4.setObjectName("pass_btn")
    self.horizontalLayout.addWidget(self.pushButton_4)
    self.comboBox = QtWidgets.QComboBox(self.centralwidget)
    self.comboBox.setObjectName("comboBox")
    self.comboBox.addItem("3")
    self.comboBox.addItem("4")
    self.comboBox.addItem("5")
    self.comboBox.addItem("6")
    self.comboBox.activated[str].connect(self.combo_text)
    self.text = ""
    self.horizontalLayout.addWidget(self.comboBox)
    self.pushButton = QtWidgets.QPushButton(self.centralwidget)
    self.pushButton.setObjectName("신청")
    self.horizontalLayout.addWidget(self.pushButton)
    self.pushButton_2 = QtWidgets.QPushButton(self.centralwidget)
    self.pushButton_2.setObjectName("신청 취소")
    self.horizontalLayout.addWidget(self.pushButton_2)
    self.pushButton_5 = QtWidgets.QPushButton(self.centralwidget)
    self.pushButton_5.setObjectName("pushButton_5")
    self.horizontalLayout.addWidget(self.pushButton_5)
    self.gridLayout.addLayout(self.horizontalLayout, 2, 2, 1, 1)
    self.horizontalLayout_3 = QtWidgets.QHBoxLayout()
    self.horizontalLayout_3.setObjectName("horizontalLayout_3")
    self.webView = QWebView()
    self.webView.setUrl(QtCore.QUrl("http://192.168.22.128:8989/Mini_Project/test/list.jsp"))
    #http://192.168.22.128:8989/Mini_Project/test/list.jsp
    #http://192.168.103.69:8091/?action=stream
    self.webView.setObjectName("webView")
    self.horizontalLayout_3.addWidget(self.webView)
    self.gridLayout.addLayout(self.horizontalLayout_3, 0, 0, 1, 3)
    mainWindow.setCentralWidget(self.centralwidget)
    self.menubar = QtWidgets.QMenuBar(mainWindow)
    self.menubar.setGeometry(QtCore.QRect(0, 0, 1116, 21))
    self.menubar.setObjectName("menubar")
    mainWindow.setMenuBar(self.menubar)
    self.statusbar = QtWidgets.QStatusBar(mainWindow)
    self.statusbar.setObjectName("statusbar")
    mainWindow.setStatusBar(self.statusbar)
    self.retranslateUi(mainWindow)
    #self.comboBox.activated['QString'].connect(mainWindow.slot1)
    self.pushButton.clicked.connect(self.pressed_request_button)
    self.pushButton_2.clicked.connect(self.inform_cancel_result)
    self.pushButton_3.clicked.connect(self.send_msg)
    self.pushButton_4.clicked.connect(self.pass_btn)
    self.pushButton_5.clicked.connect(self.exit)
    QtCore.QMetaObject.connectSlotsByName(mainWindow)
#----------------------UI 구성파트 끝-------------------------------
#----------------------UI에 들어가는 메서드------------------------
  # 콤보박스 이벤트
  def combo_text(self, text): #combobox에 값을 뽑아냄
    self.text = self.comboBox.currentText()
  # 신청버튼 이벤트
  def pressed_request_button(self, soc): #request에 버튼 누르면 combobox을 request_result_value에 저장하고, 서버로 보내기
    self.soc = client_socket
    if len(self.text) < 1 :
      self.text = '3';
    request_result_value = "/request/" + self.text
    print(request_result_value)
    self.soc.sendall(request_result_value.encode())
  # 신청 취소 이벤트
  def inform_cancel_result(self, soc):
    self.soc = client_socket
    msg = '/cancel'
    print(msg)
    self.soc.sendall(msg.encode())
  # 서버에 취소 요청
  def pass_btn(self, soc):
    self.soc = client_socket
    msg = '/stop'
    print(msg)
    self.soc.sendall(msg.encode())
  #수정1) 현재 접속한 접속자 정보 뿌려주기(UI 스레드)
  def now_users(self, msg):
    print("<현재 접속자 상태>")
    currentusers = msg.split('/')
    self.logWindow.append("<현재 접속자 상태>")
    for i in range(2, len(currentusers)):
      t = currentusers[i].split('\'')
      text = str(i-1) + ':' + str(t[1])
      self.logWindow.append(text)
  #수정2) 현재 대기큐 정보 뿌려주기(UI 스레드)
  def now_queue(self, msg):
    print("<현재 대기큐 상태>")
    queue = msg.split('/')
    self.logWindow.append("<현재 대기큐 상태>")
    for i in range(2, len(queue)):
      t = queue[i].split('\'')
      text = str(i-1) + ':' + str(t[1])
      self.logWindow.append(text)
  def receive(self, soc): # 다른 클라이언트들 메세지
    print('receive 스레드 in')
    print("type(soc)",type(soc))
    while True:
      print('while문 진입')
      data = soc.recv(1024)
      msg = data.decode()
      print("받아온 msg:", msg)
      #1) 신청 수락
      if msg.startswith("/accept"):#수락됨
        self.logWindow.append("신청이 완료되었습니다.")
      #2) 신청 거절
      elif msg.startswith("/refuse"):
        self.logWindow.append("신청이 완료되었습니다.")
      #3) PASS
      elif msg.startswith("/stop"):
        m = msg.split("/")
        if m[2] == '0':
          self.logWindow.append("중단 완료")
        else:
          self.logWindow.append("중단 실패")
      #2) 현재 접속자 정보
      if msg.startswith("/currentusers"):
        self.now_users(msg)
      #3) 현재 대기큐(순번) 정보
      elif msg.startswith("/order"):
        self.now_queue(msg)
        print()
      #4) 전체 메세지 출력
            #4) 메세지 받기
      elif msg.startswith("/msg"):
        print("<전체 메세지>")
        m = msg.split("/")
        str = m[2] + ": " + m[3]
        print(str)
        self.chatWindow.append(str)
        self.lineEdit.clear()
      elif msg.startswith("/exit"):
        m = msg.split("/")
        m = m[2].split('\'')
        text = m[1] + " 가 접속종료 하였습니다."
        self.logWindow.append(text)
# 채팅 메세지 보내기
  def send_msg(self):
    msg = '/msg/'
    msg += self.lineEdit.text()
    print('msg: ', msg)
    client_socket.sendall(msg.encode())
  def exit(self, soc):
    self.soc = client_socket
    msg = '/exit'
    print(msg)
    self.soc.sendall(msg.encode())
    #exit버튼 만들어서 나가는 신호 서버에 보내기
#------------------------------UI에 들어가는 메서드파트끝 --------------------------
  def retranslateUi(self, mainWindow):
    _translate = QtCore.QCoreApplication.translate
    mainWindow.setWindowTitle(_translate("mainWindow", "DEBATE"))
    self.pushButton_3.setText(_translate("mainWindow", "전송"))
    self.pushButton_4.setText(_translate("mainWindow", "PASS"))
    self.pushButton.setText(_translate("mainWindow", "신청"))
    self.pushButton_2.setText(_translate("mainWindow", "신청취소"))
    self.pushButton_5.setText(_translate("mainWindow", "접속종료"))
#----------------------------처음 카메라를 사용하기 위한 메서드---------------------
'''def basic_setting_new(): # 새로운 유저일떄
  os.system('sudo apt-get update')
  os.system('sudo apt-get upgrade')
  os.system('cd ~')
  os.system('mkdir mjpg')
  os.system('cd ./mjpg')
  os.system('git clone https://github.com/jacksonliam/mjpg-streamer.git')
  os.system('cd mjpg-streamer/')
  os.system('cd mjpg-streamer-experimental/')
  os.system('sudo apt-get install cmake')
  os.system('sudo apt-get install python-imaging')
  os.system('sudo apt-get install libjpeg-dev')
  os.system('make CMAKE_BUILD_TYPE=Debug')
  os.system('sudo make install')
  os.system('cd ~')
  f = open('mjpg.sh', 'w')
  f.write("export STREAMER_PATH=$HOME/mjpg/mjpg-streamer/mjpg-streamer-experimental\n")
  f.write("export LD_LIBRARY_PATH=$STREAMER_PATH\n")
  f.write("$STREAMER_PATH/mjpg_streamer -i """""input_raspicam.so"""" -o """""output_http.so -p 8091 -w $STREAMER_PATH/www""""")
  f.close()
  os.system('sh mjpg.sh')
def basic_setting_user(): # 기존 sh설치 필요 없을떄
  os.system('sh mjpg.sh')
'''
#-----------------------카메라 메서드 part끝-------------------------
if __name__ == "__main__":
  app = QtWidgets.QApplication(sys.argv)
  mainWindow = QtWidgets.QMainWindow()
  ui = Ui_mainWindow()
  ui.setupUi(mainWindow)
  print("스레드 호출")
  t1 = threading.Thread(target=ui.receive, args=(client_socket,)) # thread for send message
  t1.start()
  mainWindow.show()
  sys.exit(app.exec_())