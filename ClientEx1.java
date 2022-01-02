package programming;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.ServerSocket;

class SharedCounter2{
	int count=100;
	synchronized void decrement() {
		count--;
	}
}
class Client1 extends Socket implements Runnable{
	SharedCounter2 counter;
	public Client1(SharedCounter2 counter, String host, int Port) throws IOException {
		super(host, Port);
		this.counter = counter;
	}
	public void run() {
		//복사한 colorscript에 의해 Client1 클래스에서 SharedCounter클래스를
		//생성자로 받았고 다른 메서드인 run()에서 이 클래스의 increment()메서드에 접근.
		//일단 다 맞으니까 확인해보자. wait()-notify()쓰기전 
		//동기화 완료되었는지 확인 일단 지금까지 끝.
		for(int i=0;i<=51;i++) {
			counter.decrement();
		}
		
	}
}
class Server1 extends ServerSocket implements Runnable{
	SharedCounter2 counter;
	int Port;
	public Server1(SharedCounter2 counter, int Port) throws IOException{
		this.counter = counter;
		this.Port = Port;
	}
	public void run() {
		//client든 server든 둘 중 누군가 둘다 50번씩해서 count가 0이 된걸 받았을때
		//i는 50까지만 해버리면 0이 된걸 받는 의미가 사라져서 51까지 해서 0이 되었기에 종로
		//하도록 했음.
		for(int i=0;i<=51;i++) {
			counter.decrement();
		}
		
	}
	public Socket accept() throws IOException{
		return super.accept();
	}
}
public class ClientEx1 {
	public static void main(String[] args) throws IOException{
		SharedCounter2 counter = new SharedCounter2();
		Client1 client = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			client = new Client1(counter, "127.0.0.1",9999);
			in= new BufferedReader(new InputStreamReader(client.getInputStream()));
			out= new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			 
			while(true) {
				
				System.out.print("보내기>>");
				int count_client = counter.count;
				String outputMessage = Integer.toString(count_client);
				if(outputMessage.equalsIgnoreCase("0")) {
					out.write(outputMessage+"\n");
					out.flush();
					break;
				}
				out.write(outputMessage+"\n");
				out.flush();
				String inputMessage = in.readLine();
				//둘중 누가 0이 된 걸 받을지는 (클라이언트가 먼저 100을 받는것같기하지만)
				//모르니까 서버로부터 받은 outputMessage가 0이라면 
				//이 경우에도 종료해야 하니까 똑같이 써줌.
				if(inputMessage.equalsIgnoreCase("0")) {
					System.out.println("서버쪽에서 0이되서 서버종료됨.");
					break;
				}
				//inputMessage를 저 경우이든 아니든 출력해야하니까
				//과제제출시 지우기(바로윗 주석)
				System.out.println("서버: "+inputMessage);
				
			}
			
		}catch(IOException e) {
			//System.out.println("시스템이 client를 종료하라고 하네요.");
			System.out.println(e.getMessage());
		}finally {
			try {
				if(client != null)	client.close();
			}catch(IOException e) {
				System.out.println("서버와채팅중오류발샐");
			}
			
		}
	}
}
