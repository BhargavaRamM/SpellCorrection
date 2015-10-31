import java.util.*;
import java.lang.*;
public class KeyBoardLayout {
	public final Character[][] key = {
		{ '~', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '+'},
		{ 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']', '\\'},
		{ 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'', ' ', ' ' },
		{ 'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/', ' ', ' ' ,' ' } };
	public int getKeyboardDistance (char s, char t) {
		int x1 = 0; int y1 = 0; int x2 = 0; int y2 = 0;
		 for (int i = 0; i < 4; i++) {
			 for (int j = 0; j < 13; j++) {
				 if(key[i][j].equals(s)){
					 x1 = i;
					 y1 = j;
				    }
				 if (key[i][j].equals(t)) {
					 x2 = i;
					 y2 = j;
				 }	 
			 }
		 }		 
	int distance = Math.max(Math.abs(x1-x2),Math.abs(y1-y2));
			return distance;
		
	}
}