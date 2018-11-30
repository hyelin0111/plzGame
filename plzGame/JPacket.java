package plzGame;

import java.io.*;
import java.io.Serializable;

public class JPacket implements Serializable {
   private static final long serialVersionUID = 1L;
   public int id;       // 데이터를 보낸 사람
   public int type;    // 데이터 종류
   public String data; // 데이터 값

   public JPacket() {
   }
   
   public JPacket(int id, int type, String data) {
      this.id = id;
      this.type = type;
      this.data = data;
   }
}