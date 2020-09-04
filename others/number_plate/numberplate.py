#-*- coding:utf-8 -*-
import cv2, copy
import numpy as np
from pytesseract import *
import matplotlib.pyplot as plt
from PIL import Image
import random

possible = []
img = cv2.imread('images/test1.jpg')
copy_img=img.copy()

#<1. 이미지 전처리>
# a. 흑백처리
imggray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
# b. 윤곽선 잡기 편하게 가우시안 blur 필터 적용 (얘부터 글자 인식 실패)
blur = cv2.GaussianBlur(imggray,(3,3),0)
# c. 캐니 사용
canny = cv2.Canny(blur,100,200)

#<2. 윤곽선 추출>
contours, hierarchy = cv2.findContours(canny, cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
# a. 번호판 윤곽선 찾기
for i in range(0,len(contours)):
    cnt = contours[i]
    x,y,w,h = cv2.boundingRect(cnt)
    aspect_ratio = float(w)/h
    area = cv2.contourArea(cnt)
    if aspect_ratio >= 4 and aspect_ratio < 5: #ratio:4.5, area: 3099.0
        if area >=3000 and area < 3200:
            possible.append(contours[i])
# b. 해당 윤곽선 둘레에 사각형 그리기
cnt = possible[0]# 윤곽선 fix
x, y, w, h = cv2.boundingRect(cnt)
# c. 번호판 추출 (번호판 확대 이미지 roi 생성)
roi = img1[y:y+h,x:x+w]

# <3. 글자 인식>
# a. 흑백 이미지로 전환
gray = cv2.cvtColor(roi,cv2.COLOR_BGR2GRAY)
# b. 이진화
ret, thresh = cv2.threshold(gray,127,255,cv2.THRESH_BINARY)
# c. ERODE 작업(검은색 글자를 강조해서 더 잘 인식할 수 있게 함)
kernel = np.ones((3,3),np.uint8)
erosion = cv2.erode(thresh, kernel, iterations=1)
# d. 텍스트 검출
text = pytesseract.image_to_string(roi, lang='kor')
print(text)

# <4. 출력>
titles =['Original', 'number plate']
images =[img, erosion]

for i in range(2):
   plt.subplot(1,2,i+1), plt.title(titles[i]), plt.imshow(images[i]) #subplot(행/열/idx)
   plt.xticks([]), plt.yticks([])

plt.show()
