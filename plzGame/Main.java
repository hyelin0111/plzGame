package plzGame;

import java.io.IOException;

public class Main {
	public static void main(String[] args) 	{
		StartGame sg = new StartGame();
		try {
			MultiClient cc = new MultiClient(sg);	// 결과화면 호출 위해 startgame을 client안에 넣어버림
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}