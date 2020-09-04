import datetime
import tkinter as tk
from tkinter import messagebox  # 로그인 실패하면 에러창 띄우려고
from functools import partial

new_win = None
menu_win = None


def nothing(event):
    pass


def clear_entries(app):
    """로그아웃 시 이름/전화번호 입력칸을 비워준다"""
    app.ntr_name.delete(0, 'end')
    app.ntr_phone.delete(0, 'end')


def login(app, service, event):
    """로그인"""
    print('로그인', end=' ')
    # 입력칸으로부터 로그인 정보 가져오기
    name = app.ntr_name.get()
    phone = app.ntr_phone.get()
    flag = service.sign_in(name, phone)  # 로그인 성공 여부
    if not flag:  # 로그인 실패 시 에러창 띄우고 종료
        print('실패')
        messagebox.showerror('ERROR', "등록 정보에 존재하지 않습니다.")
        return
    elif flag == 'teacher':
        print('선생님')
        teacherWindow(app, service)
    elif flag.type == 'student':
        studentWindow(app, flag, service)
    # 로그인 버튼, 입력칸 비활성화 / 로그아웃 버튼 활성화
    app.btn_login['state'] = 'disabled'
    app.btn_login.bind('<ButtonRelease-1>', nothing)
    app.btn_logout['state'] = 'normal'
    app.btn_logout.bind('<ButtonRelease-1>', partial(logout, app, service))
    app.ntr_name['state'] = 'disabled'
    app.ntr_phone['state'] = 'disabled'


def logout(app, service, event):
    """로그아웃"""
    print('로그아웃')
    # 정보 창 닫기
    global new_win
    global menu_win
    new_win.destroy()
    new_win = None
    if menu_win is not None:
        menu_win.destroy()
        menu_win = None
    # 이름/번호 입력칸 활성화 / 기존 로그인 정보 전부 지우기
    app.ntr_name['state'] = 'normal'
    app.ntr_phone['state'] = 'normal'
    clear_entries(app)
    # 로그아웃 버튼 비활성화 / 로그인 버튼 활성화
    app.btn_logout['state'] = 'disabled'
    app.btn_logout.bind('<ButtonRelease-1>', nothing)
    app.btn_login['state'] = 'normal'
    app.btn_login.bind('<ButtonRelease-1>', partial(login, app, service))


def teacherWindow(app, service):
    """선생님께서 로그인하신 경우 새 창"""
    global new_win
    new_win = tk.Toplevel(app)
    new_win.title('Teacher')
    new_win.geometry('300x150+100+100')
    new_win.resizable(False, False)
    # 제목
    lbl = tk.Label(new_win, text='선생님', font=60)
    lbl.pack()
    # 오늘 출석 조회
    btn_look = tk.Button(new_win, width=10, font=60, text='오늘출석')
    btn_look.pack(fill='x')
    btn_look.bind('<Button-1>', partial(teacher_today, new_win, service))
    # 출결 정보 날짜 검색
    lbl_date = tk.Label(new_win, font=60, text='\n출결정보 검색')
    lbl_date.pack()
    ntr_date = tk.Entry(new_win, width=30)
    ntr_date['foreground'] = '#A1A1A1'
    ntr_date.insert(0, 'YYYY-MM-DD(EX.2020-01-01)')
    ntr_date.pack(side='left', ipady=5)
    btn_date = tk.Button(new_win, width=10, font=60, text='조회')
    btn_date.pack(side='left', fill='x')
    btn_date.bind('<Button-1>', partial(teacher_search, new_win, ntr_date, service))


def teacher_today(app, service, event):
    """'오늘 출석' 버튼 눌렀을 때 새 창"""
    global menu_win
    menu_win = tk.Toplevel(app)
    menu_win.title('Today\'s Attendance List')
    menu_win.geometry('500x300+100+100')
    menu_win.resizable(False, True)
    # 학생 리스트
    names = service.teacher_search(str(datetime.datetime.now()).split(' ')[0])
    if not names:
        # 검색된 이름이 없는 경우 --> 출석한 학생이 없는 것.
        lbl = tk.Label(menu_win, text='출석한 학생이 없습니다.', font=60)
        lbl.pack()
        return
    lbl_names = []  # 이름 출력할 레이블 리스트
    for i in range(len(names)):
        lbl_names.append(tk.Label(menu_win, text=names[i], font=60))
        lbl_names[i].pack(anchor='w')  # 출력


def teacher_search(app, entry, service, event):
    """날짜로 검색했을 때 새 창"""
    global menu_win
    menu_win = tk.Toplevel(app)
    menu_win.title('Searched by Date')
    menu_win.geometry('500x300+100+100')
    menu_win.resizable(False, True)
    # 학생 리스트
    names = service.teacher_search(entry.get())
    if names is None:
        return
    lbl_names = []
    for i in range(len(names)):
        lbl_names.append(tk.Label(menu_win, text=names[i], font=60))
        lbl_names[i].pack(anchor='w')


def studentWindow(app, student_info, service):
    """수강생이 로그인한 경우 새 창"""
    global new_win
    new_win = tk.Toplevel(app)
    new_win.title('Student')
    new_win.geometry('300x300+100+100')
    new_win.resizable(False, False)
    # 제목
    lbl = tk.Label(new_win, text='수강생', font=60)
    lbl.pack()
    # 수강생 정보
    lbl_name = tk.Label(new_win, text='이름:' + student_info.name, font=60)
    lbl_name.pack(anchor='w')
    lbl_phone = tk.Label(new_win, text='연락처:' + student_info.phone, font=60)
    lbl_phone.pack(anchor='w')
    lbl_lect = tk.Label(new_win, text='반:' + student_info.lect, font=60)
    lbl_lect.pack(anchor='w')
    # 오늘 출석 확인
    state = '출석'  # db로부터 가져와서 출석/지각/결석
    lbl_today = tk.Label(new_win, text='<오늘>' + state, font=60)
    lbl_today.pack()
    # 전체 출결 확인(1.파이그래프 띄우기, 2.총 결석일수 볼 수 있게, 3.클릭 시 시간순정보, 4.벌금계산)
    btn_check = tk.Button(new_win, font=60, text='내 출석부 보기')
    btn_check.pack(fill='x')
    btn_check.bind('<Button-1>', partial(student_check, new_win, service))


def student_check(app, service, event):
    global menu_win
    menu_win = tk.Toplevel(app)
    menu_win.title('My Attendance List')
    menu_win.geometry('300x300+100+100')
    menu_win.resizable(False, True)
    attendInfo = service.searchAttend()
    # print(attendInfo)
    lateInfo = service.searchLate()
    # print(lateInfo)
    absentInfo = service.searchAbsent()
    print(absentInfo)
    if attendInfo:
        lbl_at = []
        for i in range(len(attendInfo[0])):
            lbl_at.append(tk.Label(menu_win, text=attendInfo[0][i], font=60, fg='black'))
            lbl_at[i].pack()
    if lateInfo:
        lbl_lt = []
        for i in range(len(lateInfo[0])):
            lbl_lt.append(tk.Label(menu_win, text=lateInfo[0][i], font=60, fg='blue'))
            lbl_lt[i].pack()
    if absentInfo:
        lbl_ab = []
        for i in range(len(absentInfo)):
            lbl_ab.append(tk.Label(menu_win, text=absentInfo[i], font=60, fg='red'))
            lbl_ab[i].pack()


def make(app, service=None):
    """위젯 생성 및 배치"""
    # 이름 입력
    app.lbl_name = tk.Label(app.sub_fr, text='이름:', font=60)
    app.lbl_name.pack(anchor='w')
    app.ntr_name = tk.Entry(app.sub_fr, width=60)
    app.ntr_name.pack(fill='x', padx=5, expand=True)
    # 휴대폰 번호 입력
    app.lbl_phone = tk.Label(app.sub_fr, text='휴대폰 번호:', font=60)
    app.lbl_phone.pack(anchor='w')
    app.ntr_phone = tk.Entry(app.sub_fr, width=60)
    app.ntr_phone.pack(fill='x', padx=5, expand=True)
    # 로그인 버튼
    app.btn_login = tk.Button(app.sub_fr, width=10, font=60, text='로그인')
    app.btn_login.pack(side='left', anchor='s', padx=60, pady=5)
    app.btn_login.bind('<ButtonRelease-1>', partial(login, app, service))
    # 로그아웃 버튼
    app.btn_logout = tk.Button(app.sub_fr, width=10, font=60, text='로그아웃')
    app.btn_logout.pack(side='left', anchor='s', pady=5)
    app.btn_logout.configure(state='disabled', command=0)  # 초기상태: 비활성화