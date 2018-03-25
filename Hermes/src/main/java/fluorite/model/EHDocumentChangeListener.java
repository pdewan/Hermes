package fluorite.model;

import fluorite.commands.BaseDocumentChangeEvent;
import fluorite.commands.FileOpenCommand;

public interface EHDocumentChangeListener 
//extends DocumentChangeListener
{
	
	/**
	 * Fired when a new file was opened and the corresponding FileOpenCommand was recorded. 
	 */
	public void activeFileChanged(FileOpenCommand foc);
	
	/**
	 * Fired when a new documentChange event was recorded.
	 */
	public void documentChanged(BaseDocumentChangeEvent docChange);
	
	/**
	 * Fired when a new documentChange event was recorded and its values are fixed.
	 */
	public void documentChangeFinalized(BaseDocumentChangeEvent docChange);

}
