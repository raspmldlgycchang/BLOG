import socket
from threading import Thread

list = {}

HOST ='localhost'
PORT = 9999

#From my experiences, if I don't define this method in class definition, I don't use self.
#I'm sure that the reason is 'self' in python == 'this' constructor which meands class member in java,
#so I don't need 'self' keyword if the method is not member method for some class.
def recvMssg(sock):
    while True:
        try:
            data = sock.recv(1024)
            if not data:
                break
            print(data.decode())
        except:
            pass

#Because I will use thread to start and interrupt of some method
#regardless of method which the method I'm going to deal with belong to,
#in java, this method extends abstract method of Thread class. It is similar to that.
def runClient():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM)as sock:
        sock.connect((HOST, PORT))
        t = Thread(target=recvMssg, args=(sock,))
        t.daemon = True
        t.start()

        #< send Messg to Server : Handle > role of this method
        while True:
            msg = input("send>>")
            if msg == "/quit":
                sock.send(msg.encode())
                list["toserver"] = msg
                break
            sock.send(msg.encode())
            list["toserver"] = msg

runClient()