import socket, threading, time, os
import requests
# 딕셔너리1: ip, socket
socs = {}
# 딕셔너리2: ip, time
times = []
# 발언 대기 큐: ip
participants = []
count = 0
HOST = '0.0.0.0'
PORT = 9999  # msg, command
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((HOST, PORT))
server_socket.listen()

#iplist 파일 갱신
def new_iplist(ip_only):
    print("발언 순번 파일(iplist) update")
    f = open("C:\\Users\\Playdata\\Desktop\\workspace\\Mini Project\\WebContent\\iplist.txt","w")
    f.write(ip_only)
    f.close()
#speakerip 파일 갱신
def new_speakerip(speaker_ip):
    print("발언자 ip 파일 update")
    f = open("C:\\Users\\Playdata\\Desktop\\workspace\\Mini Project\\WebContent\\speakerip.txt","w")
    f.write(speaker_ip)
    f.close()
# 시간설정 쓰레드 함수
def timerThread():
    global count
    global times
    timer = threading.Timer(1, timerThread)
    timer.start()
    # print(count)
    if len(times) > 0:
        count += 1
        if (int(times[0][1]) * 60) == count:
            count = 0
            nextturn(ip)
            order()
# 다음 차례로 넘기기
def nextturn(ip):
    global participants
    global times
    print("next turn 함수 in.")
    if ip in participants:
        participants.remove(ip)
    for i in times:
        if i[0] == ip:
            times.remove(i)
    print(ip, "의 대기 정보가 삭제되었습니다.")
    print()
# 순번 설정
def order():
    print("순번 설정 함수 in. 새로운 순번 설정")
    ip_only = ''  # 참여자들 주소
    ip = ''  # 발언권자 주소
    # 발언 대기큐에 사람 있는 경우
    if len(participants) > 0:
        for addr in socs:
            print('addr', addr)
            if addr != participants[0]:
                tmp = str(addr).split("'")  # ip주소: tmp의 1번째 방
                ip_only += '/' + tmp[1]
            # ip_only: 모든 참여자 ip 묶음
            print(ip_only)
            new_iplist(ip_only)
            ip = str(participants[0]).split('\'')
            # sp: 발언자 ip
            sp = ip[1]
            print('speaker ip : ', sp)
            new_speakerip(sp)
    # 발언 대기큐에 사람 없는 경우
    elif len(participants) == 0:
        for addr in socs:
            print('addr:', addr)
            tmp = str(addr).split("'")  # ip주소: tmp의 1번째 방
            ip_only += '/' + tmp[1]
            # ip_only: 모든 참여자 ip
        print('ip only:',ip_only)
        new_iplist(ip_only)
    print()
# 1. 발언 신청 함수
def getrequest(ip, option):
    global times
    global participants
    print("<발언 신청>")
    # 신청 거절: 대기 큐의 마지막 ip가 해당 클라이언트 ip라면
    if len(participants) > 0 and participants[-1] == ip:
        print("신청 거절")
        s = "/refuse"
        socs.get(ip).sendall(s.encode())
        print("refuse 메세지 전송 완료")
    # 신청 수락
    else:
        print("신청 수락")
        # 대기 큐에 ip append
        participants.append(ip)
        # 대기 큐 확인
        for ip in participants:
            print(ip)
        # 딕셔너리2의 ip(키)에 time(밸류) 저장
        k = option.split('/')
        times.append([ip, k[2]])
        # 변경된 순번 웹서버에 넘기기
        order()
        # 모든 ip 합치기(발표 순번)
        s = "/accept"
        socs.get(ip).sendall(s.encode())
        time.sleep(0.1)
        print("accept 메세지 전송 완료")
        s = '/order'
        for addr in participants:
            print("addr:", addr)
            s += "/" + str(addr)
        # 모든 클라이언트에 전송
        for addr in participants:
            print("전송할 메세지:", s)
            socs.get(addr).sendall(s.encode())
            print("order 메세지 전송 완료")
        # times 딕셔너리 확인
        print("times 딕셔너리 확인")
        for key in times:
            print("ip:", key[0], " time:", key[1])
        print()
# 2. 발언 신청 쥐소 함수
def cancelrequest(ip):
    print("<발언 신청 취소>")
    for i in range(len(participants) - 1, -1, -1):
        if participants[i] == ip and i != 0:  # 현재 발언자면 취소 불가
            print("발언 취소 완료")
            s = "0"
            socs.get(ip).sendall(s.encode())
            nextturn(ip)
            # 변경된 순번 보내기 => /order로
            for addr in participants:
                s += '/' + str(addr)
            socs.get(ip).send(("/order" + s).encode())
            order()
            break
        else:
            s = "-1"
            socs.get(ip).sendall(s.encode())
    print()
# 3. 발언자 텍스트 수신 함수
def setdata(ip, option):
    if len(participants) > 0 and participants[0] == ip:
        print("발언권자 메세지")
        contents = option.split('/')[2]
        print("메세지 print:", contents)
        # 발언자 addr과 msg 함께 보내기
        for addr in socs:
            socs[addr].sendall(("/msg/" + str(ip) + "/" + contents).encode())
            print("메세지 sendall")
    else:
        print("발언권 없는 사람의 메세지")
        s = "-1"
        socs.get(ip).sendall(s.encode())
    print()
# 전체 명령
def recvCommand(ip):
    global times
    global socs
    while 1:
        # print(socs.get(ip))
        data = socs.get(ip).recv(1024)
        print(data)
        if data == b'':
            socs[ip].close()
            del (socs[ip])
            print(ip, 'Disconnect')
            break
        # print(ip)
        option = data.decode()
        if option.startswith("/request"):
            print("발언 신청 함수 호출")
            getrequest(ip, option)
        elif option.startswith("/stop"):
            print("발언 중지 함수 호출")
            # 현재 speaker면 발언 송출 중지
            if len(participants) > 0 and participants[0] == ip:
                print("발언권자 발언 취소")
                nextturn(ip)
                # 변경된 순번 보내기 => /order로
                s = ''
                for addr in participants:
                    s += str(addr)
                socs.get(ip).send(("/order" + s).encode())
                order()  # 웹에 새로운 순번 보내기
                print("발언 중단 완료. 다음 대기자에 발언권 부여")
                s = "0"
                socs.get(ip).sendall(s.encode())
            else:
                print("발언권자가 아니면 발언 취소 불가")
                s = "-1"
                socs.get(ip).sendall(s.encode())
            print()
        elif option.startswith("/cancel"):
            print("발언 신청 취소 함수 호출")
            cancelrequest(ip)
        elif option.startswith("/msg"):
            print("발언권자 msg 뿌리기 함수 호출")
            setdata(ip, option)
        elif option.startswith("/exit"):
            print(ip, " 접속 종료")
            print("next turn 함수 in.")
            for ip in participants:
                participants.remove(ip)
            for i in times:
                if i[0] == ip:
                    times.remove(i)
            print(ip, "이 대기열에서 모두 삭제되었습니다.")
            if ip in socs:
                socs[ip].close()
                del (socs[ip])
            print(ip, "소켓을 제거합니다.")
            s = '/exit/'
            s += str(ip)
            for ip in socs:
                socs[ip].sendall(s.encode())
            order()  # 웹에 새로운 순번 보내기
            break
print("server start")
timerThread()
new_speakerip("")
while True:
    client_socket, addr = server_socket.accept()
    print('Connected by', addr)
    # 딕셔너리1(ip, socket)에 추가
    socs[addr] = client_socket
    # 딕셔너리2(ip, time)에 추가
    # times[addr]=''
    ## 추가: 모든 이용자 리스트에 추가   **접속자와 순번 구별하기
    # allusers.append(str(addr))
    order()
    s = '/currentusers'

    for ip in socs:
        s += "/" + str(ip)
    # print(s)
    for ip in socs:
        socs[ip].sendall(s.encode())

    t = threading.Thread(target=recvCommand, args=(addr,))  # 쓰레드 생성.
    t.start()  # 쓰레드 실행 runnable로 만들기

server_socket.close()