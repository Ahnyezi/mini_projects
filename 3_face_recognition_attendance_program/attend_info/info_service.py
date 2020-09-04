import collections
from datetime import timedelta,datetime,time

class InfoService:
    """서비스"""
    def __init__(self, dao):
        self.dao = dao
        self.id = ''
        # self.rule_in = '09:10'
        self.rule_in = time(9, 10)

    def sign_in(self, name, phone):
        info = self.dao.sign_in(name, phone)
        if info == 'teacher':
            return info
        elif not info:
            return False
        else:
            self.id = info.id
            return info

    def teacher_search(self, date):
        # print(date)
        names = self.dao.selectByDate_t(date)
        print(names)
        return names

    # student
    def searchAttend(self):
        attendInfo = [] # 출석 리스트
        allInfo = self.dao.selectById(self.id)
        for in_date in allInfo: # 모든 입실 정보 확인
            in_time = in_date[0].split(' ')[1].split('.')[0]
            t = time(int(in_time.split(':')[0]), int(in_time.split(':')[1]))
            if t <= self.rule_in: # 규정 시간 전에 입실한 경우 출석 리스트에 추가
                attendInfo.append(in_date)
        print('atttendInfo:', attendInfo)
        return attendInfo

    def searchLate(self):
        lateInfo = [] # 지각 리스트
        allInfo = self.dao.selectById(self.id)
        for in_date in allInfo: # 모든 입실 정보 확인
            in_time = in_date[0].split(' ')[1].split('.')[0]
            t = time(int(in_time.split(':')[0]), int(in_time.split(':')[1]))
            if t > self.rule_in: # 규정 시간 후에 입실한 경우 지각 리스트에 추가
                lateInfo.append(in_date)
        print('lateInfo:', lateInfo)
        return lateInfo

    def searchAbsent(self):
        startDate = datetime.strptime('2020-07-01', '%Y-%m-%d')  # 시작일
        endDate = datetime.today()  # 오늘
        allInfo = self.dao.selectById(self.id)  # 해당 id 모든 입실시간 정보
        attendDate = collections.deque()  # date 타입의 입실시간 정보
        absentInfo = []  # 결석한 날짜 정보
        flag = True
        # str 타입의 입실시간 정보 datetime 타입으로 변환
        for in_date in allInfo:
            d = datetime.strptime(in_date[0].split(' ')[0], '%Y-%m-%d')
            attendDate.append(d)  # datetime 타입으로 append
        # 결석한 날짜 찾기
        while flag:
            # startDate와 endDate가 일치하면 반복문 종료
            if str(startDate).split(' ')[0] == str(endDate).split(' ')[0]:
                flag = False
                continue
            # attendDate의 요소가 존재할 경우
            if len(attendDate) > 0 and startDate < attendDate[0]:
                absentInfo.append(str(startDate).split(' ')[0])
                startDate += timedelta(days=1)
            elif len(attendDate) > 0 and startDate == attendDate[0]:
                attendDate.popleft()
            # attendDate의 요소가 존재하지 않을 경우
            elif len(attendDate) == 0:
                absentInfo.append(str(startDate).split(' ')[0])
                startDate += timedelta(days=1)
        print('absentInfo:', absentInfo)
        return absentInfo