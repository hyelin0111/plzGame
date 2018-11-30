package plzGame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class GameResult extends JPanel{
    ImageIcon l = new ImageIcon("img/land.png");
	Image land = l.getImage();

	ImageIcon w = new ImageIcon("img/win.png");
	Image win = w.getImage();
	
	ImageIcon lo = new ImageIcon("img/lose.png");
	Image lose = lo.getImage();

	JPanel panel = new JPanel();
	JButton restart, gowait;
	JLabel tapla;
	
	int result;
    public GameResult(int result) {
    	this.result = result;
    	
		setLayout(null);
		panel.setBackground(Color.BLACK);	
		panel.setSize(600,200);
		panel.setLocation(80,270);
        panel.setOpaque(false);
		//panel.setLayout(new FlowLayout());
		restart = new JButton(new ImageIcon("img/restart.png"));
		//restart.setSize(250,200);
		restart.setBounds(92,92,100,100);
		restart.setContentAreaFilled(false);//버튼 내용영역 채우지 않기
        restart.setFocusPainted(false);//선택되었을때 생기는 테두리 없애기
		restart.setBorderPainted(false);//버튼의 외곽선 없애기

		tapla = new JLabel("ggggggggggg");
		//배경색으로 글자색 바꿔줘서 안보이게하기
		tapla.setForeground(new Color(131,101,63));
		
		gowait = new JButton(new ImageIcon("img/gowait.png"));
		gowait.setBounds(450,320,350,150);
		gowait.setContentAreaFilled(false);//버튼 내용영역 채우지 않기
        gowait.setFocusPainted(false);//선택되었을때 생기는 테두리 없애기
		gowait.setBorderPainted(false);//버튼의 외곽선 없애기
		
		panel.add(gowait);
		panel.add(tapla);
		panel.add(restart);

        this.add(panel);
    }

    public void setResult(int num) {
    	panel.add(gowait);
		panel.add(tapla);
		panel.add(restart);

        this.add(panel);
    	result = num;
    }
    
    public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(land,0,0,getWidth(),getHeight(),this);
		
		if(result == 0)
			g.drawImage(win,150,50,500,155,this);
		else if(result == 1)
			g.drawImage(lose,190,50,400,130,this);
	}   
}