package de.unipassau.im.ontoint.proposalComputer;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.WrappedOWLEntity;

public final class CompletionProposalComputer implements
        IJavaCompletionProposalComputer {

    public static Collection<ContextFeature> getFeatures(final ContentAssistInvocationContext context) {
        String[] features;
        try {
            features = context.getDocument().get(context.getInvocationOffset() - 20, 30).split("\\s");
        } catch (BadLocationException e) {
            features = new String[0];
        }
        Collection<ContextFeature> toReturn = new LinkedList<ContextFeature>();
        for (String feature : features) {
            toReturn.add(new ContextFeature(ContextFeature.Feature.ENVIRONMENTSTRING, feature));
        }
        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public List<ICompletionProposal> computeCompletionProposals(
            ContentAssistInvocationContext context, IProgressMonitor monitor) {
        monitor.beginTask("Building proposal list.", IProgressMonitor.UNKNOWN);

        // Get the proposals from the autocomplete trie
        String toReplace = "";
        try {
            toReplace = context.computeIdentifierPrefix().toString();
        } catch (BadLocationException e1) {
            // ignore
        }
        Set<WrappedOWLEntity> proposals =
                OntointActivator.getDefault().getManager()
                .getAutocompleteTemplates(toReplace.toLowerCase());

        // Get the classifier from the plugin
        final Classifier<ContextFeature, String> classifier =
                OntointActivator.getDefault().getClassifier();

        // Define the current carret position
        final int caretPos = context.getInvocationOffset();

        // Instantiate the list to return
        final List<ICompletionProposal> toReturn =
                new LinkedList<ICompletionProposal>();

        // Wrap each proposal in an entity proposal
        for (WrappedOWLEntity proposal : proposals) {
            toReturn.add(new WrappedOWLEntityProposal(
                    proposal,
                    classifier,
                    CompletionProposalComputer.getFeatures(context),
                    caretPos,
                    caretPos - toReplace.length()));
        }

        monitor.done();
        return toReturn;
    }

    private Set<WrappedOWLEntity> getProposals(
            final ContentAssistInvocationContext context) {
        String toReplace = "";
        try {
            toReplace = context.computeIdentifierPrefix().toString();
        } catch (BadLocationException e1) {
            // ignore
        }
        return OntointActivator.getDefault().getManager()
                .getAutocompleteTemplates(toReplace.toLowerCase());
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
