import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

// Renders class progress bar in a cell
class ProgressRenderer extends JProgressBar implements TableCellRenderer {
	// Constructor
	public ProgressRenderer(int min, int max){
		super(min, max);	
	}
}