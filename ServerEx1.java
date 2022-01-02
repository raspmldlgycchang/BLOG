package programming;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ServerEx1 {
	public static void main(String[] args) {
		SharedCounter2 counter = new SharedCounter2();
		Client1 client = null;
		Server1 server = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			server = new Server1(counter,9999);
			System.out.println("연결을 기다리고 있습니다.");
			//다운 캐스팅: Socket을 Client1으로 
			client = (Client1) server.accept();
			System.out.println("연결되었어요.");
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			while(true) {
				String inputMessage = in.readLine();
				System.out.println("클라이언트: "+ inputMessage);
				if(inputMessage.equalsIgnoreCase("0")) {
					System.out.println("클라이언트쪽에서 0이 되어 종료할께요.");
					break;
				}
				System.out.print("보내기>>");
				int count_server = counter.count;
				String outputMessage = Integer.toString(count_server);
				out.write(outputMessage+"\n");
				out.flush();
			}
		}catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
}
