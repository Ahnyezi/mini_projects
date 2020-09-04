import cx_Oracle as db
import info as info


class InfoDao:
    """DAO"""

    def sign_in(self, name, phone):
        """로그인"""
        if name == 'teacher' and phone == '0000':
            return 'teacher'
        conn = db.connect("hr", "hr", "localhost:1521/xe", encoding='utf-8')  # db 연결
        cursor = conn.cursor()
        sql = 'select * from students where name=:1 and phone=:2'
        d = (name, phone)
        cursor.execute(sql, d)  # sql문 실행
        row = cursor.fetchone()  # 검색 결과 가져오기
        conn.close()  # 연결 끊기
        if row is None:
            # 검색 결과 없으면 False
            return False
        student = info.Info(row[0], row[1], row[2], row[3])  # 받아온 정보로 Info 객체 생성
        student.print_info()
        return student

    # teacher
    def selectByDate_t(self, date):  # 해당 날짜에 입실한 학생 전체
        conn = db.connect("hr", "hr", "localhost:1521/xe", encoding='utf-8')
        cursor = conn.cursor()
        # sql = 'select * from attend where in_date like :1'
        sql = 'select name from students where exists (select attend_id from attend where in_date like :1) order by id'  # 수정
        # 수정한 부분: 선생님이 출석부 명단 볼 때 아이디가 아닌 이름으로 보게 하기 위함. attend 테이블에 따로 이름컬럼 추가할 필요x
        d = ('%' + date + '%',)
        cursor.execute(sql, d)
        names = []
        for name in cursor:
            names.append(list(name))  # 수정
        # # 추가시작
        # if not names: return
        # sql = 'select * from attend where in_date like :1 order by attend_id'
        # cursor.execute(sql, d)
        # row = cursor.fetchall()
        # print(row)
        # print(len(row))
        # print(names)
        # for i in range(len(row)):
        #     names[i].append(row[i][1])
        # names.sort(key=lambda x: x[1])  # 출석한 시간 순서대로 정렬
        # # 추가끝
        conn.close()
        return names

    # students
    def selectById(self, id):  # 해당 id의 입실정보 전체
        conn = db.connect("hr", "hr", "localhost:1521/xe", encoding='utf-8')
        cursor = conn.cursor()
        sql = "select in_date from attend where attend_id =:1"
        d = (id,)
        cursor.execute(sql, d)
        attendInfo = []
        if cursor is not None:
            for date in cursor:
                attendInfo.append(date)
        return attendInfo

    def selectByDate_s(self, id, date):  # 특정날짜 본인 입실정보
        conn = db.connect("hr", "hr", "localhost:1521/xe", encoding='utf-8')
        cursor = conn.cursor()
        sql = 'select in_date from attend where attend_id =:1 and in_date like :2'
        d = (id, '%' + date + '%')
        cursor.execute(sql, d)
        row = cursor.fetchone()
        conn.close
        if row is not None:
            return row[0]  # 입실 시간 반환
