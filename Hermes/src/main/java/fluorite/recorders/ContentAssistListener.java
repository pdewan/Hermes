package fluorite.recorders;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionListenerExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;

public class ContentAssistListener implements ICompletionListener {

	@Override
	public void assistSessionStarted(ContentAssistEvent event) {
		System.out.println("Session started" + event.toString());
//		// TODO Auto-generated method stub
		
	}

	@Override
	public void assistSessionEnded(ContentAssistEvent event) {
//		try {
//		System.out.println("Session ended" + event.assistant.showPossibleCompletions());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
	}

	@Override
	public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
		// TODO Auto-generated method stub
		try {
		System.out.println("Selection changed" + proposal.getDisplayString());
		System.out.println("Selection contxt" + proposal.getContextInformation());

		
//		System.out.println(proposal.getAdditionalProposalInfo());
//		if (proposal instanceof ICompletionProposalExtension2) {
//			ICompletionProposalExtension2 anExtension2 = (ICompletionProposalExtension2) proposal;
//			
//		}
//		System.out.println(proposal.getContextInformation().getInformationDisplayString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	static ContentAssistListener instance;
	
	public static ContentAssistListener getInstance() {
		if (instance == null) {
			instance = new ContentAssistListener();
		}
		return instance;
	}

}
