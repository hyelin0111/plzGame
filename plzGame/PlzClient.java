package plzGame;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
 
class MultiClientThread extends Thread {
	private MultiClient mc;
	static int dia = 21;	// 보석
	static int drl = 31;	// 드릴 업그레이드
	static int trap = 61;	// 함정

	public MultiClientThread(MultiClient mc) {
		this.mc = mc;
	}
	
	public static void enterRoom(int roomNum) {	// GameWait에서 방번호 입력 후 ok 버튼누르면 호출됨
		try {
			MultiClient.oos.writeObject(new JPacket(MultiClient.id, -1, roomNum+""));// 서버로 방 탐색 메세지 보냄
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void makeItem() {
		for (int i = 0; i < 15; i++) { // 드릴 업, 배치 1~10개 나옴
			while (true) {
				int x = (int) (Math.random() * 29);
				int y = (int) (Math.random() * 49);
				if (Game.taple[x][y] == 11) {
					Game.taple[x][y] = drl;
					try {
						MultiClient.oos.writeObject(new JPacket(MultiClient.id, 2, x + "#" + y + "#" + drl));
					} catch (IOException e) {
						// TODO 자동 생성된 catch 블록
						e.printStackTrace();
					} // 맵 상태 서버로 보냄
					break;
				}
			}
		}

		for (int i = 0; i < 20; i++) { // 함정, 15개에서 20개로 늘림 많은 시연이 필요
			while (true) {
				int x = (int) (Math.random() * 29);
				int y = (int) (Math.random() * 49);
				if (Game.taple[x][y] == 11) {
					Game.taple[x][y] = trap;
					try {
						MultiClient.oos.writeObject(new JPacket(MultiClient.id, 2, x + "#" + y + "#" + trap));
					} catch (IOException e) {
						// TODO 자동 생성된 catch 블록
						e.printStackTrace();
					} // 맵 상태 서버로 보냄
					break;
				}
			}
		}

		while (true) { // 보석, 배치 무조건 맨 마지막
			int x = (int) (Math.random() * 29);
			int y = (int) (Math.random() * 49);
			if (Game.taple[x][y] == 11) {
				Game.taple[x][y] = dia;
				try {
					MultiClient.oos.writeObject(new JPacket(MultiClient.id, 2, x + "#" + y + "#" + dia));
				} catch (IOException e) {
					// TODO 자동 생성된 catch 블록
					e.printStackTrace();
				} // 맵 상태 서버로 보냄
				break;
			}
		}
	}
	
	@Override
	public void run() {
		JPacket message = null;
		boolean isStop = false;
		while (!isStop) {
			try {
				message = (JPacket) mc.getOis().readObject();	// 서버에서 메세지 받아옴
				System.out.println("received msg type : " + message.type);
				System.out.println("received msg data : " + message.data);
				if (message.type == 0) {// 상대방 좌표
					String tmp[] = message.data.split("#");	// 구분자
					Game.y1 = Integer.parseInt(tmp[0]);		// 좌표
					Game.x1 = Integer.parseInt(tmp[1]);		// 좌표			
					Game.p2char = Integer.parseInt(tmp[2]);	// 2p 방향
					Game.e_drealer = Integer.parseInt(tmp[3]);	// 보석 상태
					
					if (Game.taple[Game.y1][Game.x1] == 15 || Game.taple[Game.y1][Game.x1] == 25)	// 2p가 15(보석), 25(드릴)위치에 가면 아이템 없어지게 함
						Game.taple[Game.y1][Game.x1] = 5;	// 흙으로 값을 바꿔줌
	
					if (Game.y1==Game.ix && Game.x1==Game.jx && Game.drealer[1]!=0)	{ // 보석을 가지고 있는 상태에서 상대랑 좌표가 같으면 상대방에게 보석 전달
						MultiClient.oos.writeObject(new JPacket(MultiClient.id, 3, ""));	// 서버에게 알려줌
						Game.drealer[1] = 0;
					}
				}
				if (message.type == -2) {	// 방 매칭이 됨
					MultiClient.oos.writeObject(new JPacket(MultiClient.id, -4, ""));	// 상대방이 방에 들어왔다고 서버에 알려줌(먼저들어온 애는 나중에 들어온 애의 소켓을 가지고 있지 않기 떄문에 알려줘야 함)
					MultiClient.is1p = true;	// 2p가 들어왔다는 것을 저장
					Game.x1 = 1;
					Game.y1 = 3;
					Game.x = 46 * 15;
					Game.y = 23 * 15;
					Game.ix = 26;
					Game.jx = 47;
					
					GameWait.btnStart.setEnabled(true);	// 2명이 한 방에 들어오게 되면 스타트 버튼 활성화
					
					makeItem();
					Game.taple[3][0] = 13;	// 빨간문과 파란문의 이미지 위치 바꿔줌
					Game.taple[26][48] = 14;
				} 
				if(message.type == 5) {	// 서버에서 ThreadID 받아옴. 쓰레드가 많이 만들어지니까 구분하기 위해 받아옴.
					MultiClient.id = message.id;
				}
				if (message.type == -5) {	// 채팅
					GameWait.content.append(message.data + "\n");	// TextArea에 채팅 내용 추가
					GameWait.content.setCaretPosition(GameWait.content.getDocument().getLength());	// 채팅 내용 추가돼도 포커스를 최신 글에 맞춰줌
				}
				if (message.type == 2) {	// 맵 상태
					String tmp[] = message.data.split("#");	// 구분자
					int ix = Integer.parseInt(tmp[0]);		// 배열 인덱스
					int jx = Integer.parseInt(tmp[1]);		// 배열 인덱스
					int state = Integer.parseInt(tmp[2]);	// 해당 위치의 값
					if(state!=14 && state!=13)
						Game.taple[ix][jx] = state;	// 받아온 값으로 맵의 상태를 바꿔줌
				}
				if (message.type == -3) {	// 방이 꽉 찬 경우
					GameWait.alert();		// 경고창 띄워줌
				}
				if (message.type == -6) {	// 방 입장에 성공한 경우(두명이 한방을 선택한 경우)
					GameWait.btnStart.setEnabled(true);	// 스타트 버튼 활성화
					GameWait.btnOk.setEnabled(false);	// ok 버튼 비활성화
				}
				if (message.type == -7) {	// 방 만들기에 성공한 경우
					GameWait.btnOk.setEnabled(false);	// ok 버튼 비활성화
					GameWait.notification();	// 알림창 띄워줌
				}
				if (message.type == 4) {	// 승패 구분
					StartGame.result = true;
					Game.mapReset();	// 진 사람 맵 초기화
					mc.sg.getContentPane().removeAll();
					mc.sg.gr.setResult(1);
					mc.sg.setContentPane(mc.sg.gr);	// 졌을 때
					mc.sg.revalidate();
					mc.sg.repaint();
					//StartGame.PrintResult();
				}
				if(message.type == 6) {
					StartGame.isEnded = false;
					mc.sg.getContentPane().removeAll();	// 프레임에 있던 패널 지움
					mc.sg.setBackground(new Color(97, 73, 43));	// 뒷 배경 색 지정
					mc.sg.setContentPane(mc.sg.game);	// 게임 화면 패널 붙임	
					mc.sg.requestFocus();
					mc.sg.revalidate();
					mc.sg.repaint();
				}
				if (message.type == 3) {	// 보석
					Game.drealer[1] = 1;	// 상대방에게서 보석 뺏어와서 저장
					Game.e_drealer = 0;		// 상대방의 보석 상태 없음으로 바꿔줌
				}
				if (message.type == 7) {	// 대기방 가기
					StartGame.isEnded = true;
					mc.sg.getContentPane().removeAll();
					mc.sg.gw.panel_repaint();
					mc.sg.gw.roomNum.setText("");
					mc.sg.gw.btnOk.setEnabled(true);
					mc.sg.gw.btnStart.setEnabled(false);
					MultiClient.is1p = false;
					mc.sg.gr.setResult(0);
					StartGame.result = false;
					Game.reset();
					mc.sg.setContentPane(mc.sg.gw);
					mc.sg.revalidate();
					mc.sg.repaint();
				}
				if (message.type == 8) {	// 재시작
					mc.sg.getContentPane().removeAll();	
					Game.reset();
					mc.sg.setContentPane(new Game());
					mc.sg.revalidate();
					mc.sg.repaint();
				}
			} catch (Exception e) {
				System.exit(0);
				e.printStackTrace();
			}
		}
	}
}
 
class MultiClient {
	//public static final String IP = "192.168.0.22";
	public static final String IP = "localhost";
	public static final int PORT = 9000;
	private Socket socket;
	private ObjectInputStream ois;
	static ObjectOutputStream oos;
	static int id;					// 서버 안에서 플레이어 구분하기 위한 변수
	static boolean is1p = false;	
	StartGame sg;
	public MultiClient(StartGame sg) throws IOException {
		this.sg = sg;
		socket = new Socket(IP, PORT);
		System.out.println("connected...");
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
		MultiClientThread ct = new MultiClientThread(this);
		Thread t = new Thread(ct);
		t.start();
	}
 
	public ObjectInputStream getOis() {
		return ois;
	}
}