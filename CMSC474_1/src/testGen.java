import java.math.*;
import java.util.Random;
public class testGen {
	public static void main(String[] args){
		for (int i = 1; i <= 3; i++){
			for (int a = 1; a <= 3; a++){
				for (int b = 1; b <= 3; b++){
					for (int c = 1; c <= 3; c++){
						for (int d = 1; d <= 5; d++){
							Random rn = new Random();
							rn.nextInt(10);
							System.out.print(i + " " + a + " "+ b +" " + c + " " + d + " ");
							//System.out.print(rn.nextInt(3) + " " + rn.nextInt(3) + " " + rn.nextInt(3) + " " + rn.nextInt(3) + " " + rn.nextInt(3) + " ");
						}
						System.out.println();
					}
				}
			}
		}
	}
}
