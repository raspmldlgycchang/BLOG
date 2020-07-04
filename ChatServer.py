import socketserver
import threading
from threading import Thread

lock = threading.Lock()
HOST = ''
PORT = 9999

class UserManager:
    def __init__(self):
        self.users = {}
    def addUser(self, username, conn, addr):
        if username in self.users:
            conn.send('이미 등록된 사용자입니다.\n'.encode())
            return None
        lock.acquire()
        self.users[username] = (conn, addr)
        lock.release()

        self.sendToAll("[%s]님 입장하셨습니다.\n"%username)

        return username
    def removeUser(self,username):
        if username not in self.users:
            return
        lock.acquire()
        del self.users[username]
        lock.release()

        self.sendToAll("[%s]님 퇴장하셨습니다.\n"%username)
    def msgHandler(self, username, msg):
        if msg[0] != '/':
            self.sendToAll('[%s]님: %s'%(username, msg))
            return
        if msg.strip() == '/quit':
            self.removeUser(username)
            return -1
    def sendToAll(self, msg):
        for conn, addr in self.users.values():
            conn.send(msg.encode())
class TCPHandlerClass(socketserver.BaseRequestHandler):
    #def __init__(self, server_address, RequestHandlerClass, 생략): BaseServer클래스의 생성자함수
        #initialization
    #object create
    usermaan = UserManager()

    #BaseRequestHandler는 생성자함수가 인자가 request, client_address, self로 3개면 TCPHandler클래스가  이 클래스를 상속받게
    #하면 돼.

    def handle(self):
        print('[%s]님 연결됨'%self.client_address[0])

        try:
            username = self.registerUsername()
            msg = self.request.recv(1024)
            while msg:
                print(msg.decode())
                if self.usermaan.msgHandler(username, msg.decode()) == -1:
                    self.request.close()
                    break
                msg = self.request.recv(1024)
        except Exception as e:
            print(e)

    def registerUsername(self):
        while True:
            self.request.send('로그인ID:'.encode())
            username = self.request.recv(1024)
            username = username.decode().strip()
            if self.usermaan.addUser(username, self.request, self.client_address):
                return username

class ChatingServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    pass
def runServer():
    print("채팅서버시작합니다.")
    print("채팅서버끝내려면 Ctrl+C누르세요.")

    try:
        server = ChatingServer((HOST,PORT), TCPHandlerClass)
        server.serve_forever()
    except KeyboardInterrupt:
        print("채팅서버종료합니다.")
        #blocks server_forever loops until the loops is finished. this must be called while server_forever() is running
        #in another thread, or it will deadlock.
        server.shutdown()
        #called to clean-up the server.
        server.server_close()

runServer()
