package plzGame;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

import java.io.IOException;

public class GameWait extends JPanel implements ActionListener {
	ImageIcon ic = new ImageIcon("img/land.png");
	Image land = ic.getImage();

	ImageIcon t = new ImageIcon("img/title3.png");
	Image title = t.getImage();

	ImageIcon r = new ImageIcon("img/num.png");
	Image room = r.getImage();

	static ImageIcon i = new ImageIcon("img/start.png");
	static Image start = i.getImage();

	ImageIcon a = new ImageIcon("img/rule.png");
	Image rule = a.getImage();

	ImageIcon gr = new ImageIcon("img/gamer.png");
	Image gamer = gr.getImage();

	JPanel panel = new JPanel();
	static JButton btnStart;
	static JTextField roomNum;
	static JButton btnOk;

	static JPanel jp = new JPanel();
	JTextField chatting;
	static JTextArea content;
	JScrollPane jsp;
	String name;
	String number;

	public GameWait() {
		setLayout(null);

		jp.setLocation(25, 80);
		jp.setSize(550, 400);
		jp.setLayout(new BorderLayout());
		content = new JTextArea("", 20, 20);
		content.setEditable(false);
		chatting = new JTextField(50);
		jsp = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		content.setBackground(new Color(132, 102, 25));
		content.setForeground(Color.WHITE);

		panel.setLocation(570, 100);
		panel.setSize(200, 800);
		panel.setOpaque(false); // false로 해야 투명
		roomNum = new JTextField(9);
		
		btnOk = new JButton(new ImageIcon("img/ok.png"));
		btnOk.setPreferredSize(new Dimension(40, 23));
		btnOk.setContentAreaFilled(false);
		btnOk.setFocusPainted(false);
		btnOk.setBorderPainted(false);

		btnStart = new JButton(new ImageIcon("img/start.png"));
		btnStart.setContentAreaFilled(false);// 버튼 내용영역 채우지 않기
		btnStart.setFocusPainted(false);// 선택되었을때 생기는 테두리 없애기
		btnStart.setBorderPainted(false);// 버튼의 외곽선 없애기
		// btnStart.setEnabled(false);

		panel.add(roomNum);
		panel.add(btnOk);
		panel.add(btnStart);
		// 채팅창 붙인것
		jp.add(jsp, BorderLayout.CENTER);
		jp.add(chatting, BorderLayout.SOUTH);

		this.add(panel);
		this.add(jp);

		number = roomNum.getText();
		if (number == null) {
			btnStart.setEnabled(false);
		}
		chatting.addActionListener(this);
	} // 생성자
	
	public void panel_repaint() {
		panel.add(roomNum);
		panel.add(btnOk);
		panel.add(btnStart);
		// 채팅창 붙인것
		jp.add(jsp, BorderLayout.CENTER);
		jp.add(chatting, BorderLayout.SOUTH);

		this.add(panel);
		this.add(jp);
	}
	public void setName(String name) {
		this.name = name;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(land, 0, 0, getWidth(), getHeight(), this);
		g.drawImage(title, 580, 10, 180, 70, this);
		g.drawImage(room, 600, 75, 150, 20, this);
		g.drawImage(rule, 600, 220, 130, 50, this);
		g.drawImage(gamer, 615, 290, 140, 200, this);

		g.setColor(Color.WHITE);
		g.drawLine(610, 280, 610, 500);// 왼쪽세로선
		g.drawLine(610, 280, 750, 280);// 위쪽가로선
		g.drawLine(750, 280, 750, 500);// 오른쪽세로선
		g.drawLine(610, 500, 750, 500);// 아래쪽가로선
	}
	public static void alert() {
		JOptionPane.showMessageDialog(jp, "방이 꽉 찼습니다", "경고", JOptionPane.WARNING_MESSAGE);
		roomNum.setText("");	// 이미 2명의 플레이어가 들어간 방의 경우 경고 메세지를 띄워주고 다른 번호를 입력하게 함. 
		btnStart.setEnabled(false);	// 스타트 버튼 비활성화
	}
	
	public static void notification() {
		JOptionPane.showMessageDialog(jp, "방 만들기 성공", "알림", JOptionPane.PLAIN_MESSAGE);	// 방을 처음 만든 경우 방 만들기 성공 메세지 띄워줌
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == chatting) {
			if (chatting.getText() == null || chatting.getText().length() == 0) {	// 채팅창에 아무것도 입력하지 않고 엔터를 누르게 되면 경고메세지 띄워줌
				JOptionPane.showMessageDialog(this, "글을 쓰세요", "경고", JOptionPane.WARNING_MESSAGE);
			} else {
				try {
					MultiClient.oos.writeObject(new JPacket(0, -5, name + " : " + chatting.getText()));	// 서버로 채팅 내용 보냄
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				chatting.setText("");	// 채팅 내용 입력후 텍스트 필드 초기화
			}
		}
	}
}