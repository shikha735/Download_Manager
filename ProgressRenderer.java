import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

// Renders class progress bar in a cell
class ProgressRenderer extends JProgressBar implements TableCellRenderer {
	// Constructor
	public ProgressRenderer(int min, int max){
		super(min, max);	
	}
	
	// Returns JProgressBar as renderer for the given table cell
	public Component getTableCellRendererComponent(Jtable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		// Setting the progress bar's value
		setValue((int) ((Float)value).floatValue());
		return this;
	}
}