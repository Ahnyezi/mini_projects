class Info:
    """수강생 정보"""

    def __init__(self, id, name, phone, lect=None):
        self.type = 'student'
        self.id = id  # id(이름+전화번호뒷자리)
        self.name = name  # 이름
        self.phone = phone  # 전화번호
        self.lect = lect  # 반(class 못 써서 lecture의 lect로 함!)

    def print_info(self):
        print('이름:' + self.name, end=' / ')
        print('연락처:' + self.phone, end=' / ')
        print('반:' + self.lect)
