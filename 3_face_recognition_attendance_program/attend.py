import cx_Oracle
import numpy as np
import cv2
import os
import time
import threading
from datetime import datetime


class FaceDetect:
    def __init__(self):
        self.font = cv2.FONT_HERSHEY_SIMPLEX
        self.trained_data = 'classifier/trainer.yml'
        self.recognizer = cv2.face.LBPHFaceRecognizer_create()
        self.classifier = cv2.CascadeClassifier('classifier/haarcascade_frontalface_default.xml')


        # self.RecordForTrainingData()
        # 학습시킬 얼굴을 녹화하는 작업.

        # self.face_recog_train()
        # dataset에 있는 사진들은 trainer.yml에 학습이 되어있음.
        # 데이터를 dataset에 추가하고 train하면 데이터를 추가하여 trainer.xml에서 재학습

        self.names = os.listdir('dataset/')

        self.attendance = {}
        self.getStudentName('인공지능')

        self.flag=False


    def getStudentName(self, classname):
        conn = cx_Oracle.connect("hr", "hr", "localhost:1521/xe", encoding='utf-8')
        cursor = conn.cursor()
        sql = 'select id from students where class=:1'
        d = (classname, )
        cursor.execute(sql, d)
        for row in cursor:
            self.attendance[row[0]] = False

        sql = 'select attend_id from ATTEND, STUDENTS where in_date like :1 and attend.attend_id=students.id and students.class=:2'
        d = (str(datetime.now()).split(' ')[0]+'%', classname)
        cursor.execute(sql, d)
        for row in cursor:
            self.attendance[row[0]] = True
        conn.close()
        print("출석표", self.attendance)


    def RecordForTrainingData(self):    # training data를 위해 영상 촬영
        n = input('입력 : ')
        os.mkdir('./dataset/' + n)
        cap = cv2.VideoCapture(0)
        train_switch = False
        i = 0

        while True:
            ret, frame = cap.read()
            if not ret:
                break

            if train_switch:
                frame2 = frame.copy()
                cv2.putText(frame, "recording", (50, 50), self.font, 1, (0, 0, 255), 2)
                cv2.circle(frame, (300, 200), 150, (0, 255, 0), 3)
                cv2.imshow('frame', frame)
                cv2.imwrite('annotation/'+n+'/'+n+str(i)+'.jpg', frame2)
                i += 1
                if cv2.waitKey(1) & 0xFF == ord('c') or i>500:
                    break
                time.sleep(0.05)
            else:
                cv2.circle(frame, (300, 200), 150, (0, 255, 0), 3)
                cv2.imshow('frame', frame)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    train_switch = True
        cap.release()
        cv2.destroyAllWindows()

    def face_recog_train(self):     # 데이터 학습 시키기
        dataset_path = 'dataset/'
        dirs = os.listdir(dataset_path)  # 'dataset/' 하위 디렉토리 이름을 리스트에 저장

        people = []
        for dir in dirs:
            # 'dataset/사람이름명폴더/'에 저장된 파일명들을 people에 담음
            if os.path.isdir(dataset_path + dir):
                people.append(os.listdir(dataset_path + dir))
        recognizer = cv2.face.LBPHFaceRecognizer_create()
        samples = []
        ids = []
        # 학습할 얼굴 샘플링
        for id, row in enumerate(people):
            for p in row:
                img = cv2.imread(dataset_path + dirs[id] + '/' + p)
                print(dirs[id])
                print(p)
                gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
                face = self.classifier.detectMultiScale(
                    gray,
                    scaleFactor=1.2,
                    minNeighbors=5,
                    minSize=(20, 20)
                )
                for (x, y, w, h) in face:
                    samples.append(gray[y:y + h, x:x + w])
                    ids.append(id)
        recognizer.train(samples, np.array(ids))
        recognizer.write(self.trained_data)
        print('얼굴 학습 완료')

    def face_detect(self):
        info = ''
        self.recognizer.read(self.trained_data)

        cap = cv2.VideoCapture(0)

        possible = 0    # loss가 30이하로 20번 확인되면 출석 인정
        pname = ''  # 체크할 이름

        while True:
            global frame
            ret, frame = cap.read()
            cv2.circle(frame, (300, 200), 150, (0, 255, 0), 3)

            if not ret:
                break
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = self.classifier.detectMultiScale(gray, 1.3, 5)
            # cv2.putText(frame, info, (5, 15), self.font, 0.5, (255, 0, 255), 1)
            if len(faces) == 1:
                x, y, w, h = faces[0]
                cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 0), 2)
                # cv2.putText(frame, "Detected Face", (x - 5, y - 5), self.font, 0.5, (255, 255, 0), 2)
                id, loss = self.recognizer.predict(gray[y:y + h, x:x + w])
                # loss가 100보다 작은지 확인: 0이면 완전 일치!
                name = self.names[id]

                if loss < 100:
                    if loss <= 40:
                        if not pname:
                            pname = name
                            possible = 1
                        elif pname==name:
                            possible += 1
                            if possible == 15:
                                self.flag = True
                                threading.Thread(target=self.attend_chk, args=(name, x, y)).start()
                                threading.Thread(target=self.flag_switch).start()
                                if not self.attendance[name]:
                                    threading.Thread(target=self.upload, args=(name, )).start()
                                pname = ''
                                possible = 0
                        elif pname!=name:
                            pname = name
                            possible = 1


                print(name, '/ loss:', loss)
                cv2.putText(frame, name, (x+30, y - 5), self.font, 1, (204, 000, 153), 2)

            cv2.imshow('frame', frame)
            k = cv2.waitKey(30)
            if k == 27:
                break
        cap.release()
        cv2.destroyAllWindows()

    def attend_chk(self, name, x, y):
        global frame
        while self.flag:
            if self.attendance[name]:
                cv2.circle(frame, (300, 200), 150, (0, 0, 255), 3)
                cv2.putText(frame, name + " already attend", (50, 425), self.font, 1, (0, 0, 255), 2)
            else:
                cv2.circle(frame, (300, 200), 150, (0, 0, 255), 3)
                cv2.putText(frame, name + " PASS", (150, 425), self.font, 1, (204, 000, 153), 2)

    def flag_switch(self):
        time.sleep(2)
        self.flag=False

    def upload(self, name):
        self.attendance[name] = True
        conn = cx_Oracle.connect("hr", "hr", "localhost:1521/xe", encoding='utf-8')
        cursor = conn.cursor()
        sql = 'insert into ATTEND values (:1, :2)'
        d = (name, str(datetime.now()))
        cursor.execute(sql, d)
        conn.commit()
        conn.close()

if __name__ == '__main__':
    t = FaceDetect()
    t.face_detect()