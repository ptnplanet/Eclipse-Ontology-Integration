package de.unipassau.im.ontoint.proposalComputer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class CompletionProposalComputer implements
        IJavaCompletionProposalComputer {

    public CompletionProposalComputer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void sessionStarted() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<ICompletionProposal> computeCompletionProposals(
            ContentAssistInvocationContext context, IProgressMonitor monitor) {
        return new LinkedList<ICompletionProposal>();
    }

    @Override
    public List<IContextInformation> computeContextInformation(
            ContentAssistInvocationContext context, IProgressMonitor monitor) {
        return new LinkedList<IContextInformation>();
    }

    @Override
    public String getErrorMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sessionEnded() {
        // TODO Auto-generated method stub

    }

}
