package MCTSCCGame;
import javax.swing.SwingUtilities;

public class ChineseCheckers {

	public static void main(String[] args) {

	      // Run GUI codes in the Event-Dispatching thread for thread safety
	      SwingUtilities.invokeLater(new Runnable() {
	         @Override
	         public void run() {
	        	 new GUI();
	         }
	      });

	}

}