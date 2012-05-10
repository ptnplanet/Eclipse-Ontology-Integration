package de.unipassau.im.ontoint.proposalComputer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.WrappedOWLEntity;

public final class CompletionProposalComputer implements
        IJavaCompletionProposalComputer {

    /**
     * {@inheritDoc}
     */
    public List<ICompletionProposal> computeCompletionProposals(
            ContentAssistInvocationContext context, IProgressMonitor monitor) {
        monitor.beginTask("Building proposal list.", IProgressMonitor.UNKNOWN);

        final int caretPos = context.getInvocationOffset();
        String toReplace;
        try {
            toReplace = context.computeIdentifierPrefix().toString();
        } catch (BadLocationException e1) {
            toReplace = "";
        }
        toReplace = toReplace.toLowerCase();
        Set<WrappedOWLEntity> proposals = OntointActivator.getDefault()
                .getManager().getAutocompleteTemplates(toReplace);
        final List<ICompletionProposal> toReturn =
                new LinkedList<ICompletionProposal>();

        for (WrappedOWLEntity proposal : proposals) {
            toReturn.add(new WrappedOWLEntityProposal(proposal, caretPos, caretPos - toReplace.length()));
        }

        monitor.done();
        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public List<IContextInformation> computeContextInformation(
            final ContentAssistInvocationContext context,
            final IProgressMonitor monitor) {
        return new LinkedList<IContextInformation>();
    }

    /**
     * {@inheritDoc}
     */
    public String getErrorMessage() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void sessionStarted() { }

    /**
     * {@inheritDoc}
     */
    public void sessionEnded() { }

}
