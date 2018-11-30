package plzGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class StartGame extends JFrame {
	ImageIcon l = new ImageIcon("img/land.png");
	Image land = l.getImage();

	ImageIcon t = new ImageIcon("img/title3.png");
	Image title = t.getImage();

	ImageIcon es = new ImageIcon("img/start11.png");
	Image notst = es.getImage();

	JLabel input;	
	JTextField tf;
	JButton next;
	String name;
	GameWait gw = new GameWait();
	String rn = gw.roomNum.getText();
	static boolean isEnded = true;	// 게임이 끝나면 키입력 안받게(맵이 지워지는 것 막음)
	static boolean result = false;
	static GameResult gr;
	static Game game;
	public StartGame() {
		game = new Game();
		gr = new GameResult(0);
		this.setTitle("땅좀파조");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		MyPanel panel = new MyPanel();
		panel.setLayout(null);

		input = new JLabel("이름 입력 : ");
		input.setForeground(Color.WHITE);
		input.setBounds(220, 240, 70, 50);

		tf = new JTextField(null, 15);
		tf.setBounds(300, 255, 100, 20);

		next = new JButton(new ImageIcon("img/next.png"));
		next.setBounds(180, 300, 400, 150);
		next.setContentAreaFilled(false);
		next.setFocusPainted(false);
		next.setBorderPainted(false);

		panel.add(input);
		panel.add(tf);
		panel.add(next);

		this.add(panel);
		this.setBounds(500, 100, 790, 540);

		this.setResizable(false);
		this.setVisible(true);

		// 시작화면에 있는 next버튼 눌렀을 때
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton bt = (JButton) e.getSource();
				name = tf.getText(); // 입력한 유저 이름을 name 변수에 저장
				if (bt == next && !name.equals("")) {// 빈칸이아니면넘어가기
					System.out.println("NEXT눌렀다.!!");
					getContentPane().removeAll();	// 프레임에 있던 패널을 지움
					gw.setName(name); // GameWait의 setName 메소드를 이용해서 유저 이름 저장(채팅에 이용)
					setContentPane(gw);	// 대기화면 패널 붙임
					revalidate();
					repaint();
					System.out.println(name);
				} else if (bt == next && (name.equals("") || name.equals("이름입력하세요"))) {
					// tf.setText("이름입력하세요");
					JOptionPane.showMessageDialog(null, "이름 입력하세요", "!!!!", JOptionPane.WARNING_MESSAGE);
					tf.requestFocus();
				}
			}
		});
		
		// 대기화면에 있는 start버튼 눌렀을 때
		gw.btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton sbtn = (JButton) e.getSource();
				String rNum = gw.roomNum.getText();
				if (sbtn == gw.btnStart && !rNum.equals("")) {
					isEnded = false;
					System.out.println("나는 Start야");
					try {
						MultiClient.oos.writeObject(new JPacket(MultiClient.id, 6, ""));
					} catch (IOException e1) {
						// TODO 자동 생성된 catch 블록
						e1.printStackTrace();
					}	// 게임 시작
					getContentPane().removeAll();	// 프레임에 있던 패널 지움
					setBackground(new Color(97, 73, 43));	// 뒷 배경 색 지정
					setContentPane(game);	// 게임 화면 패널 붙임	
					requestFocus();
					revalidate();
					repaint();
					System.out.println(rNum);
					System.out.println(name);
				} else {
					System.out.println("번호입력!!!");
				}
			}
		});
		
		// 대기화면에 있는 ok버튼 눌렀을 때
		gw.btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton ko = (JButton) e.getSource();
				rn = gw.roomNum.getText();
				if (ko == gw.btnOk && !rn.equals("")) {
					System.out.println("나는 Ok야");
					System.out.println(name);
					MultiClientThread.enterRoom(Integer.parseInt(rn)); // ok버튼 누르면 방탐색을 위해 PlzClient에 있는 enterRoom 메소드 호츌
				} else {
					System.out.println("입력해라!");
				}
			}
		});
		
		if (rn.equals("")) {
			// start버튼비활성화
			gw.btnStart.setEnabled(false);
		}
		
		// 대기화면에서 방번호 입력 받기 / isDigit() - 숫자인지 아닌지 판단 숫자면 true, 아니면 false판단
		gw.roomNum.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
					getToolkit().beep();
					e.consume();
					JOptionPane.showMessageDialog(null, "숫자만입력하자!!", "!!!!", JOptionPane.WARNING_MESSAGE);
					System.out.println("숫자만입력해");
				}
			}
		});
		
		// 시작화면에서 이름 영어만 입력받기
		tf.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if ((Character.isDigit(c))) {
					getToolkit().beep();
					e.consume();
					JOptionPane.showMessageDialog(null, "ONLY ENGLISH", "ENGLISH ONLY!", JOptionPane.WARNING_MESSAGE);
					System.out.println("영어만!!!!");
				}
			}
		});
		
		// 결과화면에서 재시작 눌렀을 때
		gr.restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton gobtn = (JButton) e.getSource();
				if (gobtn == gr.restart) {
					isEnded = false;
					getContentPane().removeAll();
					try {
						MultiClient.oos.writeObject(new JPacket(MultiClient.id, 8, ""));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Game.reset();
					setContentPane(new Game());
					requestFocus();
					revalidate();
					repaint();
				}
			}
		});
		
		// 결과화면에서 대기방가기 눌렀을 때
		gr.gowait.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton gobtn = (JButton) e.getSource();
				if (gobtn == gr.gowait) {
					isEnded = true;
					try {
						MultiClient.oos.writeObject(new JPacket(MultiClient.id, 7, ""));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					getContentPane().removeAll();
					gw.panel_repaint();
					gw.roomNum.setText("");
					gw.btnOk.setEnabled(true);
					gw.btnStart.setEnabled(false);
					MultiClient.is1p = false;
					gr.setResult(0);
					StartGame.result = false;
					Game.reset();
					setContentPane(gw);
					revalidate();
					repaint();
				}
				System.out.println("대기방가자!");
			}
		});

		// --------------------------- key event ---------------------------//
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(!isEnded) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_UP:
						if (Game.drealer[0] != 0) { // Game.drealer 0번에 +100으로 0이 될때 까지 못나옴
							Game.drealer[0]--;
						} else {
							if (Game.taple[Game.ix - 1][Game.jx] == 5) {
								Game.y -= 15;
								Game.ix--;
							} else if (Game.taple[Game.ix - 1][Game.jx] == 11 || Game.taple[Game.ix - 1][Game.jx] == 21
									|| Game.taple[Game.ix - 1][Game.jx] == 31 || Game.taple[Game.ix - 1][Game.jx] == 41
									|| Game.taple[Game.ix - 1][Game.jx] == 61) { // 흙 1번 파기 파기
								/////////////// 드릴 설명서 //////////////////
								if (Game.drealer[2] == 0) { // 캐릭 드릴 NO UP
									Game.taple[Game.ix - 1][Game.jx]--; // 한번에 한번 파짐
								} else if (Game.drealer[2] == 1) { // 캐릭 드릴 1UP
									Game.taple[Game.ix - 1][Game.jx] = Game.taple[Game.ix - 1][Game.jx] - 2; // 한번에 두번 파짐
								} else if (Game.drealer[2] == 2) { // 캐릭 드릴 2UP
									Game.taple[Game.ix - 1][Game.jx] = Game.taple[Game.ix - 1][Game.jx] - 3; // 한번에 세번 파짐
								} else if (Game.drealer[2] == 3) { // 캐릭 드릴 3UP
									Game.taple[Game.ix - 1][Game.jx] = Game.taple[Game.ix - 1][Game.jx] - 4; // 한번에 네번 파짐
								} else { // 이상
									Game.taple[Game.ix - 1][Game.jx] = Game.taple[Game.ix - 1][Game.jx] - 5; // 한번에 다섯번 파짐
								}
								//////////////////// 땅 파는 과정 ////////////////////////
							} else if (Game.taple[Game.ix - 1][Game.jx] == 10 || Game.taple[Game.ix - 1][Game.jx] == 20
									|| Game.taple[Game.ix - 1][Game.jx] == 30 || Game.taple[Game.ix - 1][Game.jx] == 40
									|| Game.taple[Game.ix - 1][Game.jx] == 60) { // 흙 두 번째 파기
								Game.taple[Game.ix - 1][Game.jx]--;
							} else if (Game.taple[Game.ix - 1][Game.jx] == 9 || Game.taple[Game.ix - 1][Game.jx] == 19
									|| Game.taple[Game.ix - 1][Game.jx] == 29 || Game.taple[Game.ix - 1][Game.jx] == 39
									|| Game.taple[Game.ix - 1][Game.jx] == 59) { // 흙 세 번째 파기
								Game.taple[Game.ix - 1][Game.jx]--;
							} else if (Game.taple[Game.ix - 1][Game.jx] == 8 || Game.taple[Game.ix - 1][Game.jx] == 18
									|| Game.taple[Game.ix - 1][Game.jx] == 28 || Game.taple[Game.ix - 1][Game.jx] == 38
									|| Game.taple[Game.ix - 1][Game.jx] == 58) { // 흙 네 번째 파기
								Game.taple[Game.ix - 1][Game.jx]--;
							} else if (Game.taple[Game.ix - 1][Game.jx] == 7 || Game.taple[Game.ix - 1][Game.jx] == 17
									|| Game.taple[Game.ix - 1][Game.jx] == 27 || Game.taple[Game.ix - 1][Game.jx] == 37
									|| Game.taple[Game.ix - 1][Game.jx] == 57) { // 흙 다섯 번재 파기
								Game.taple[Game.ix - 1][Game.jx]--;
							} else if (Game.taple[Game.ix - 1][Game.jx] == 6 || Game.taple[Game.ix - 1][Game.jx] == 16
									|| Game.taple[Game.ix - 1][Game.jx] == 26 || Game.taple[Game.ix - 1][Game.jx] == 36
									|| Game.taple[Game.ix - 1][Game.jx] == 56) { // 흙 여섯 번째 파기
								Game.taple[Game.ix - 1][Game.jx]--;
							}
							try { // 맵 상태 서버로 보냄
								MultiClient.oos.writeObject(new JPacket(MultiClient.id, 2,
										(Game.ix - 1) + "#" + Game.jx + "#" + Game.taple[Game.ix - 1][Game.jx]));
							} catch (IOException e1) {
								// TODO 자동 생성된 catch 블록
								e1.printStackTrace();
							}
							if (Game.taple[Game.ix - 1][Game.jx] == 15) { // 보석 소장 Game.drealer 1번에 소장
								Game.y -= 15;
								Game.taple[Game.ix - 1][Game.jx] = 5;
								Game.ix--;
								Game.drealer[1] = 1;
							} else if (Game.taple[Game.ix - 1][Game.jx] == 25) { // 드릴 UP Game.drealer 2번에 ++
								Game.y -= 15;
								Game.taple[Game.ix - 1][Game.jx] = 5;
								Game.ix--;
								Game.drealer[2]++;
							} else if (Game.taple[Game.ix - 1][Game.jx] == 55) { // 함정 Game.drealer 0번에 +100으로 0이 될때 까지 못나옴
								Game.taple[Game.ix - 1][Game.jx] = 56;
								Game.y -= 15;
								Game.ix--;
								Game.drealer[0] += 100;
							}
						}
						Game.p1char = 4;
						break;
	
					case KeyEvent.VK_DOWN:
						if (Game.drealer[0] != 0) {// Game.drealer 0번에 +100으로 0이 될때 까지 못나옴
							Game.drealer[0]--;
						} else {
							if (Game.taple[Game.ix + 1][Game.jx] == 5) {
								Game.y += 15;
								Game.ix++;
							} else if (Game.taple[Game.ix + 1][Game.jx] == 11 || Game.taple[Game.ix + 1][Game.jx] == 21
									|| Game.taple[Game.ix + 1][Game.jx] == 31 || Game.taple[Game.ix + 1][Game.jx] == 41
									|| Game.taple[Game.ix + 1][Game.jx] == 61) { // 흙 한번 파기 파기
								/////////////// 드릴 설명서 //////////////////
								if (Game.drealer[2] == 0) {
									Game.taple[Game.ix + 1][Game.jx]--;
								} else if (Game.drealer[2] == 1) {
									Game.taple[Game.ix + 1][Game.jx] = Game.taple[Game.ix + 1][Game.jx] - 2;
								} else if (Game.drealer[2] == 2) {
									Game.taple[Game.ix + 1][Game.jx] = Game.taple[Game.ix + 1][Game.jx] - 3;
								} else if (Game.drealer[2] == 3) {
									Game.taple[Game.ix + 1][Game.jx] = Game.taple[Game.ix + 1][Game.jx] - 4;
								} else {
									Game.taple[Game.ix + 1][Game.jx] = Game.taple[Game.ix + 1][Game.jx] - 5; // 위 내용과 같습니다.																								// 이하 동문
								}
								//////////////////// 땅 파는 과정 ////////////////////////
							} else if (Game.taple[Game.ix + 1][Game.jx] == 10 || Game.taple[Game.ix + 1][Game.jx] == 20
									|| Game.taple[Game.ix + 1][Game.jx] == 30 || Game.taple[Game.ix + 1][Game.jx] == 40
									|| Game.taple[Game.ix + 1][Game.jx] == 60) { // 흙 두 번째 파기
								Game.taple[Game.ix + 1][Game.jx]--;
							} else if (Game.taple[Game.ix + 1][Game.jx] == 9 || Game.taple[Game.ix + 1][Game.jx] == 19
									|| Game.taple[Game.ix + 1][Game.jx] == 29 || Game.taple[Game.ix + 1][Game.jx] == 39
									|| Game.taple[Game.ix + 1][Game.jx] == 59) { // 흙 세 번째 파기
								Game.taple[Game.ix + 1][Game.jx]--;
							} else if (Game.taple[Game.ix + 1][Game.jx] == 8 || Game.taple[Game.ix + 1][Game.jx] == 18
									|| Game.taple[Game.ix + 1][Game.jx] == 28 || Game.taple[Game.ix + 1][Game.jx] == 38
									|| Game.taple[Game.ix + 1][Game.jx] == 58) { // 흙 네 번째 파기
								Game.taple[Game.ix + 1][Game.jx]--;
							} else if (Game.taple[Game.ix + 1][Game.jx] == 7 || Game.taple[Game.ix + 1][Game.jx] == 17
									|| Game.taple[Game.ix + 1][Game.jx] == 27 || Game.taple[Game.ix + 1][Game.jx] == 37
									|| Game.taple[Game.ix + 1][Game.jx] == 57) { // 흙 다섯 번째 파기
								Game.taple[Game.ix + 1][Game.jx]--;
							} else if (Game.taple[Game.ix + 1][Game.jx] == 6 || Game.taple[Game.ix + 1][Game.jx] == 16
									|| Game.taple[Game.ix + 1][Game.jx] == 26 || Game.taple[Game.ix + 1][Game.jx] == 36
									|| Game.taple[Game.ix + 1][Game.jx] == 56) { // 흙 여섯 번째 파기
								Game.taple[Game.ix + 1][Game.jx]--;
							}
							try { // 맵 상태 서버로 보냄
								MultiClient.oos.writeObject(new JPacket(MultiClient.id, 2,
										(Game.ix + 1) + "#" + Game.jx + "#" + Game.taple[Game.ix + 1][Game.jx]));
							} catch (IOException e1) {
								// TODO 자동 생성된 catch 블록
								e1.printStackTrace();
							}
							if (Game.taple[Game.ix + 1][Game.jx] == 15) { // 보석 소장 Game.drealer 1번에 소장
								Game.y += 15;
								Game.taple[Game.ix + 1][Game.jx] = 5;
								Game.ix++;
								Game.drealer[1] = 1;
							} else if (Game.taple[Game.ix + 1][Game.jx] == 25) { // 드릴 UP Game.drealer 2번에 ++
								Game.y += 15;
								Game.taple[Game.ix + 1][Game.jx] = 5;
								Game.ix++;
								Game.drealer[2]++;
							} else if (Game.taple[Game.ix + 1][Game.jx] == 55) { // 함정 100 번 움직여야 빠짐
								Game.taple[Game.ix + 1][Game.jx] = 56;
								Game.y += 15;
								Game.ix++;
								Game.drealer[0] += 100; // 함정 Game.drealer 0번에 +100으로 0이 될때 까지 못나옴
							}
						}
						Game.p1char = 1;
						break;
					case KeyEvent.VK_LEFT:
						if (Game.drealer[0] != 0) {// Game.drealer 0번에 +100으로 0이 될때 까지 못나옴
							Game.drealer[0]--;
						} else {
							if (Game.taple[Game.ix][Game.jx - 1] == 5) {
								Game.x -= 15;
								Game.jx--;
							} else if (Game.taple[Game.ix][Game.jx - 1] == 11 || Game.taple[Game.ix][Game.jx - 1] == 21
									|| Game.taple[Game.ix][Game.jx - 1] == 31 || Game.taple[Game.ix][Game.jx - 1] == 41
									|| Game.taple[Game.ix][Game.jx - 1] == 61) {// 흙 파기
								/////////////// 드릴 설명서 //////////////////
								if (Game.drealer[2] == 0) { // 드릴 속도
									Game.taple[Game.ix][Game.jx - 1]--;
								} else if (Game.drealer[2] == 1) {
									Game.taple[Game.ix][Game.jx - 1] = Game.taple[Game.ix][Game.jx - 1] - 2;
								} else if (Game.drealer[2] == 2) {
									Game.taple[Game.ix][Game.jx - 1] = Game.taple[Game.ix][Game.jx - 1] - 3;
								} else if (Game.drealer[2] == 3) {
									Game.taple[Game.ix][Game.jx - 1] = Game.taple[Game.ix][Game.jx - 1] - 4;
								} else {
									Game.taple[Game.ix][Game.jx - 1] = Game.taple[Game.ix][Game.jx - 1] - 5; // 이하 동문
								}
								//////////////////// 땅 파는 과정 ////////////////////////
							} else if (Game.taple[Game.ix][Game.jx - 1] == 10 || Game.taple[Game.ix][Game.jx - 1] == 20
									|| Game.taple[Game.ix][Game.jx - 1] == 30 || Game.taple[Game.ix][Game.jx - 1] == 40
									|| Game.taple[Game.ix][Game.jx - 1] == 60) { // 흙 두 번째 파기
								Game.taple[Game.ix][Game.jx - 1]--;
							} else if (Game.taple[Game.ix][Game.jx - 1] == 9 || Game.taple[Game.ix][Game.jx - 1] == 19
									|| Game.taple[Game.ix][Game.jx - 1] == 29 || Game.taple[Game.ix][Game.jx - 1] == 39
									|| Game.taple[Game.ix][Game.jx - 1] == 59) { // 흙 세 번째 파기
								Game.taple[Game.ix][Game.jx - 1]--;
							} else if (Game.taple[Game.ix][Game.jx - 1] == 8 || Game.taple[Game.ix][Game.jx - 1] == 18
									|| Game.taple[Game.ix][Game.jx - 1] == 28 || Game.taple[Game.ix][Game.jx - 1] == 38
									|| Game.taple[Game.ix][Game.jx - 1] == 58) { // 흙 네 번째 파기
								Game.taple[Game.ix][Game.jx - 1]--;
							} else if (Game.taple[Game.ix][Game.jx - 1] == 7 || Game.taple[Game.ix][Game.jx - 1] == 17
									|| Game.taple[Game.ix][Game.jx - 1] == 27 || Game.taple[Game.ix][Game.jx - 1] == 37
									|| Game.taple[Game.ix][Game.jx - 1] == 57) { // 흙 다석 번째 파기
								Game.taple[Game.ix][Game.jx - 1]--;
							} else if (Game.taple[Game.ix][Game.jx - 1] == 6 || Game.taple[Game.ix][Game.jx - 1] == 16
									|| Game.taple[Game.ix][Game.jx - 1] == 26 || Game.taple[Game.ix][Game.jx - 1] == 36
									|| Game.taple[Game.ix][Game.jx - 1] == 56) { // 흙 여석 번째 파기
								Game.taple[Game.ix][Game.jx - 1]--;
							}
							try { // 맵 상태 서버로 보냄
								MultiClient.oos.writeObject(new JPacket(MultiClient.id, 2,
										Game.ix + "#" + (Game.jx - 1) + "#" + Game.taple[Game.ix][Game.jx - 1]));
							} catch (IOException e1) {
								// TODO 자동 생성된 catch 블록
								e1.printStackTrace();
							}
							if (Game.taple[Game.ix][Game.jx - 1] == 15) { // 보석 먹기
								Game.x -= 15;
								Game.taple[Game.ix][Game.jx - 1] = 5;
								Game.jx--;
								Game.drealer[1] = 1;
							} else if (Game.taple[Game.ix][Game.jx - 1] == 25) { // 드릴 UP Game.drealer 2번에 ++
								Game.x -= 15;
								Game.taple[Game.ix][Game.jx - 1] = 5;
								Game.jx--;
								Game.drealer[2]++;
							} else if (Game.taple[Game.ix][Game.jx - 1] == 55) { // 함정 Game.drealer 0번에 +100으로 0이 될때 까지 못나옴
								Game.taple[Game.ix][Game.jx - 1] = 56;
								Game.x -= 15;
								Game.jx--;
								Game.drealer[0] += 100;
							} else if (Game.taple[Game.ix][Game.jx - 1] == 13) { // 출구 나가기
								if (Game.drealer[1] == 1) {
									Game.x -= 15;
									Game.jx--;
									
									Game.mapReset();
									try {
										MultiClient.oos.writeObject(new JPacket(MultiClient.id, 4, ""));
									} catch (IOException e1) {
										// TODO 자동 생성된 catch 블록
										e1.printStackTrace();
									}
									getContentPane().removeAll();
									gr.setResult(0);
									setContentPane(gr);
									revalidate();
									repaint();
								}
							}
						}
						Game.p1char = 2;
						break;
					case KeyEvent.VK_RIGHT:
						if (Game.drealer[0] != 0) {// Game.drealer 0번에 +100으로 0이 될때 까지 못나옴
							Game.drealer[0]--;
						} else {
							if (Game.taple[Game.ix][Game.jx + 1] == 5) {
								Game.x += 15;
								Game.jx++;
							} else if (Game.taple[Game.ix][Game.jx + 1] == 11 || Game.taple[Game.ix][Game.jx + 1] == 21
									|| Game.taple[Game.ix][Game.jx + 1] == 31 || Game.taple[Game.ix][Game.jx + 1] == 41
									|| Game.taple[Game.ix][Game.jx + 1] == 61) { // 흙 파기
								/////////////// 드릴 설명서 //////////////////
								if (Game.drealer[2] == 0) { // 드릴 속도
									Game.taple[Game.ix][Game.jx + 1]--;
								} else if (Game.drealer[2] == 1) {
									Game.taple[Game.ix][Game.jx + 1] = Game.taple[Game.ix][Game.jx + 1] - 2;
								} else if (Game.drealer[2] == 2) {
									Game.taple[Game.ix][Game.jx + 1] = Game.taple[Game.ix][Game.jx + 1] - 3;
								} else if (Game.drealer[2] == 3) {
									Game.taple[Game.ix][Game.jx + 1] = Game.taple[Game.ix][Game.jx + 1] - 4;
								} else {
									Game.taple[Game.ix][Game.jx + 1] = Game.taple[Game.ix][Game.jx + 1] - 5; // 이하 동문
								}
								//////////////////// 땅 파는 과정 ////////////////////////
							} else if (Game.taple[Game.ix][Game.jx + 1] == 10 || Game.taple[Game.ix][Game.jx + 1] == 20
									|| Game.taple[Game.ix][Game.jx + 1] == 30 || Game.taple[Game.ix][Game.jx + 1] == 40
									|| Game.taple[Game.ix][Game.jx + 1] == 60) { // 흙 두 번째 파기
								Game.taple[Game.ix][Game.jx + 1]--;
							} else if (Game.taple[Game.ix][Game.jx + 1] == 9 || Game.taple[Game.ix][Game.jx + 1] == 19
									|| Game.taple[Game.ix][Game.jx + 1] == 29 || Game.taple[Game.ix][Game.jx + 1] == 39
									|| Game.taple[Game.ix][Game.jx + 1] == 59) { // 흙 세 번째 파기
								Game.taple[Game.ix][Game.jx + 1]--;
							} else if (Game.taple[Game.ix][Game.jx + 1] == 8 || Game.taple[Game.ix][Game.jx + 1] == 18
									|| Game.taple[Game.ix][Game.jx + 1] == 28 || Game.taple[Game.ix][Game.jx + 1] == 38
									|| Game.taple[Game.ix][Game.jx + 1] == 58) { // 흙 네 번째 파기
								Game.taple[Game.ix][Game.jx + 1]--;
							} else if (Game.taple[Game.ix][Game.jx + 1] == 7 || Game.taple[Game.ix][Game.jx + 1] == 17
									|| Game.taple[Game.ix][Game.jx + 1] == 27 || Game.taple[Game.ix][Game.jx + 1] == 37
									|| Game.taple[Game.ix][Game.jx + 1] == 57) { // 흙 다섯 번째 파기
								Game.taple[Game.ix][Game.jx + 1]--;
							} else if (Game.taple[Game.ix][Game.jx + 1] == 6 || Game.taple[Game.ix][Game.jx + 1] == 16
									|| Game.taple[Game.ix][Game.jx + 1] == 26 || Game.taple[Game.ix][Game.jx + 1] == 36
									|| Game.taple[Game.ix][Game.jx + 1] == 56) { // 흙 여섯 번째 파기
								Game.taple[Game.ix][Game.jx + 1]--;
							}
							try { // 맵 상태 서버로 보냄
								MultiClient.oos.writeObject(new JPacket(MultiClient.id, 2,
										Game.ix + "#" + (Game.jx + 1) + "#" + Game.taple[Game.ix][Game.jx + 1]));
							} catch (IOException e1) {
								// TODO 자동 생성된 catch 블록
								e1.printStackTrace();
							}
							if (Game.taple[Game.ix][Game.jx + 1] == 15) { // 보석 먹기
								Game.x += 15;
								Game.taple[Game.ix][Game.jx + 1] = 5;
								Game.jx++;
								Game.drealer[1] = 1;
							} else if (Game.taple[Game.ix][Game.jx + 1] == 25) { // 드릴 UP Game.drealer 2번에 ++
								Game.x += 15;
								Game.taple[Game.ix][Game.jx + 1] = 5;
								Game.jx++;
								Game.drealer[2]++;
							} else if (Game.taple[Game.ix][Game.jx + 1] == 55) { // 함정 Game.drealer 0번에 +100으로 0이 될때 까지 못나옴
								Game.taple[Game.ix][Game.jx + 1] = 56;
								Game.x += 15;
								Game.jx++;
								Game.drealer[0] += 100;
							} else if (Game.taple[Game.ix][Game.jx + 1] == 13) { // 출구 나가기
								if (Game.drealer[1] == 1) {
									Game.x += 15;
									Game.jx++;
									Game.mapReset(); // 이긴 사람 맵 초기화
									try {
										MultiClient.oos.writeObject(new JPacket(MultiClient.id, 4, ""));
									} catch (IOException e1) {
										// TODO 자동 생성된 catch 블록
										e1.printStackTrace();
									}
									getContentPane().removeAll();
									gr.setResult(0);
									setContentPane(gr);
									revalidate();
									repaint();
								}
							}
						}
						Game.p1char = 3;
						break;
					}
					try {
	//					if(Game.preX != Game.ix && Game.preY != Game.jx ) {	// 트랩 걸렸을 때 부필요한 패킷을 서버로 보내지 않게 함
							MultiClient.oos.writeObject(new JPacket(MultiClient.id, 0, Game.ix + "#" + Game.jx + "#" + Game.p1char + "#" + Game.drealer[1]));
	//						Game.preX = Game.ix;
	//						Game.preY = Game.jx;
	//					}
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					repaint();
				}
			}
		});
	}// 생성자

	public class MyPanel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(land, 0, 0, getWidth(), getHeight(), this);// 땅이미지
			g.drawImage(title, 90, 60, 500, 200, this);
		}
	}
}