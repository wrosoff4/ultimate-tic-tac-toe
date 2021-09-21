/*
 * @author: Will Rosoff
 * @Title: Ultimate Tic Tac toe
 * @Purpose: Runs the fun game
 * @Date: 11-20-2020
 */
import javax.swing.SwingUtilities;

public class TestUltimateTCT {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			  public void run() {
				  new UltimateTCT();
			  }
	});

	}

}
