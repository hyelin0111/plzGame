package plzGame;

import java.net.*;
import java.io.*;
import java.util.*;

// 방 탐색 				/ -1
// 방 매칭				/ -2
// 방이 꽉참              		/ -3
// 상대방이 방에 들어옴 	/ -4
// 채팅				/ -5
// 방 입장 성공			/ -6
// 방 만들기 성공			/ -7
// 게임 종료 			/ -9
// x, y 좌표  			/ 0
// 아이템 사용 			/ 1
// 맵 상태				/ 2
// 보석  				/ 3
// 승패				/ 4
// 플레이어 구분			/ 5
// 게임 시작			/ 6
// 대기방 가기			/ 7
// 재시작				/ 8
class MultiServerThread implements Runnable {
	private Socket socket; 
	private PlzServer ms;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;		// 자신
	private ObjectOutputStream oos2;	// 같은 방에 있는 상대방
	private int ThreadID;

	public MultiServerThread(PlzServer ms, int ThreadID) {
		this.ms = ms;
		this.ThreadID = ThreadID;
	}

	@Override
	public synchronized void run() {
		boolean isStop = false;
		int currentRoom = -1;
		try {
			socket = ms.getSocket(); // 접속한 클라이언트의 소켓 저장
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			JPacket message = null;
			oos.writeObject(new JPacket(ThreadID, 5, ""));	// type = 5		=> 		플레이어 구분
			while (!isStop) {
				message = (JPacket) ois.readObject();		// 패킷 읽음
				System.out.println("msg id : " + message.id);
				System.out.println("msg type : " + message.type);
				System.out.println("msg data : " + message.data);
				if (message.type == -9)						// type = -9		=> 		게임 완전 종료
					break; 
				if (message.type == -4) { 					// type = -4		=> 		상대방이 방에 들어옴
					System.out.println("p2입장");
					oos2 = MatchRoom.room[currentRoom].po2;	// 플레이어 1이 플레이어 2의 소켓을 저장
				} 
				if (message.type == -5) {					// type = -5		=> 		채팅
					broadCasting(message);
				} else {
					send(message);							// 채팅 아니면 상대방한테 메시지 전송
				}
//				if (message.type == 4) { 					// type = 4		=> 		게임 승패 표시로 넘어감
//					//isStop = true;
//				} 
				if (message.type == 7) {	// 대기방 가기
					for (int i=0; i<=MatchRoom.maxroom; i++) {			// 만들어진 방들 중에서
						if (i == currentRoom) {	// 게임 끝나면 현재 있던 방을 없앰
							MatchRoom.room[i].play = false;
							MatchRoom.room[i].po1 = null;
							MatchRoom.room[i].po2 = null;
							MatchRoom.room[i].roomName = 0;
							currentRoom = -1;
							System.out.println("방 제거" + i);
						}
					}
				}
				if (message.type == -1) { 			// type = -1		=> 		방 탐색
					boolean findRoom = false;
					int roomNum = -1;
					for (int i=0; i<MatchRoom.maxroom; i++) { // 만들어진 방들 중에서
						if (MatchRoom.room[i].roomName == Integer.parseInt(message.data)) { // 입력한 방번호가 있으면
							findRoom = true; // 방을 찾았다고 저장
							roomNum = i; // 배열의 몇 번째 방인지 저장
						}
					}
					if (findRoom == true) { // 입력한 방번호의 방을 찾으면(이미 있는 방에 들어가는 경우)
						if (MatchRoom.room[roomNum].play == false) { // 2명이 모두 들어가지 않은 경우
							MatchRoom.room[roomNum].po2 = oos; // 플레이어2의 소켓 저장(본인 소켓 저장)
							MatchRoom.room[roomNum].play = true; // 2명이 들어갔으니까 play를 true로 바꿈
							oos2 = MatchRoom.room[roomNum].po1; // 플레이어1의 소켓을 저장(상대방 소켓 저장)
							send(new JPacket(ThreadID, -2, roomNum + "")); // 방 매칭으로 넘어감. 상대방한테 방매칭됐다고 알려줌
							oos.writeObject(new JPacket(ThreadID, -6, roomNum + ""));	// 방 입장 성공 알려줌
						} else {
							oos.writeObject(new JPacket(ThreadID, -3, roomNum + ""));	// 방이 꽉 찼을 경우 (한 방에 두 명이 들어갔거나 게임이 시작 중일 때)
						}
					} else { // 못 찾은 경우(방이 없는 경우)
						MatchRoom.room[MatchRoom.maxroom] = new Room(); // 방을 만들어줌
						MatchRoom.room[MatchRoom.maxroom].po1 = oos; // 플레이어1의 소켓 저장
						MatchRoom.room[MatchRoom.maxroom].roomName = Integer.parseInt(message.data); // 입력한 방의 이름 저장
						currentRoom = MatchRoom.maxroom; // 현재 방의 이름 저장(게임 끝나면 방 지우기 위해 저장)
						System.out.println("dddd : " + currentRoom);
						MatchRoom.maxroom++; // 만들어진 방의 개수 1 증가시킴
						oos.writeObject(new JPacket(ThreadID, -7, roomNum + ""));		// 방 만들기 성공
					}
				}

			}
		} catch (Exception e) {
			for (int i=0; i<=MatchRoom.maxroom; i++) {			// 클라이언트 종료시 만들어진 방들 중에서
				if (i == currentRoom) {	// 게임 끝나면 현재 있던 방을 없앰
					MatchRoom.room[i].play = false;
					MatchRoom.room[i].po1 = null;
					MatchRoom.room[i].po2 = null;
					MatchRoom.room[i].roomName = 0;
				}
			}
			ms.getList().remove(this);	// 접속 종료 시 리스트에서 삭제
			e.printStackTrace();
			System.out.println(socket.getInetAddress() + "비정상적으로 종료하셨습니다");
			System.out.println("list size : " + ms.getList().size());
//		} finally {
//			try {
//				if (ois != null)
//					ois.close();
//				if (oos != null)
//					oos.close();
//				if (oos2 != null)
//					oos2.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	public void broadCasting(JPacket message) throws IOException {	// 접속한 모든 클라이언트한테 메시지 전송(채팅)
		for (MultiServerThread ct : ms.getList()) {
			ct.oos.writeObject(message);
		}
	}

	public void send(JPacket message) throws IOException {	// 같은 방인 상대한테만 메시지 전송(게임)
		//oos.writeObject(message);
		if (oos2 != null) {
			System.out.println("sd id : " + message.id);
			System.out.println("sd type : " + message.type);
			System.out.println("sd data : " + message.data);
			oos2.writeObject(message);
		}
	}
}

public class PlzServer {
	private final static int SERVER_PORT = 9000;
	private ArrayList<MultiServerThread> list;
	private Socket socket;

	public PlzServer() throws IOException {
		list = new ArrayList<MultiServerThread>();
		ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
		MultiServerThread mst = null;
		boolean isStop = false;
		int ThreadID = 0;	// 클라이언트에 번호 부여해서 플레이어 구분
		while (!isStop) {
			System.out.println("Server read...");
			socket = serverSocket.accept(); // 클라이언트 접속할 때마다 소켓 연결
			mst = new MultiServerThread(this, ThreadID++);// 클라이언트 접속할 때마다 쓰레드 생성
			list.add(mst);
			Thread t = new Thread(mst);	// Thread를 직접 상속받지 못하고 Runnable을 implements했기 때문에 Thread 객체로 바꾸는 작업 필요
			t.start();
		}
	}

	public ArrayList<MultiServerThread> getList() {
		return list;
	}

	public Socket getSocket() {
		return socket;
	}

	public static void main(String args[]) throws IOException {
		new PlzServer();
	}
}

//class SocketList {
//	public static int num = 0;
//	public static ArrayList<Integer> i = new ArrayList<Integer>();
//	public static ArrayList<Socket> socket = new ArrayList<Socket>();
//}

class MatchRoom extends Thread {
	//static ArrayList<Integer> waitplayer = new ArrayList<Integer>();
	static Room[] room = new Room[100]; // 방은 최대 100개까지 생성 가능
	static int maxroom = 0;				// 만들어진 방의 개수
}

class Room {
	int roomName;					// 입력한 방의 번호
	boolean play = false;			// 하나의 방에 두 명이 모이면 play = true로 표시
	ObjectOutputStream po1, po2;	// 플레이어1, 플레이어2의 소켓	
}