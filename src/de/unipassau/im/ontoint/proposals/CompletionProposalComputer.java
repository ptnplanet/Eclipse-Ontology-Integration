package de.unipassau.im.ontoint.proposals;

import java.util.Collection;
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

/**
 * Proposals for the eclipse ContentAssists System from the ontology integration
 * are computed with this class.  Basically the class extracts the featureset
 * from the {@link ContentAssistInvocationContext} and creates a list of
 * {@link WrappedOWLEntityProposal} wrapping the proposals from the model's
 * prefix trie.
 *
 * @author Philipp Nolte
 */
public final class CompletionProposalComputer implements
        IJavaCompletionProposalComputer {

    /**
     * Extracts a collection of features from the given context.
     *
     * @param context the context
     * @return the featureset
     */
    public static Collection<ContextFeature> getFeatures(
            final ContentAssistInvocationContext context) {

        String[] features;
        try {
            features = context.getDocument().get(context.getInvocationOffset() - 20, 20).split("\\s");
        } catch (BadLocationException e) {
            features = new String[0];
        }
        Collection<ContextFeature> toReturn = new LinkedList<ContextFeature>();

        ContextFeature feature = new ContextFeature(ContextFeature.Feature.ENVIRONMENTSTRING, features[features.length - 1]);
        toReturn.add(feature);
        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public List<ICompletionProposal> computeCompletionProposals(
            final ContentAssistInvocationContext context,
            final IProgressMonitor monitor) {
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

        // Define the current caret position
        final int caretPos = context.getInvocationOffset();

        // Get the featureset.
        final Collection<ContextFeature> features =
                CompletionProposalComputer.getFeatures(context);

        // Instantiate the list to return
        final List<ICompletionProposal> toReturn =
                new LinkedList<ICompletionProposal>();

        // Wrap each proposal in an entity proposal
        for (WrappedOWLEntity proposal : proposals) {
            toReturn.add(new WrappedOWLEntityProposal(
                    proposal,
                    classifier,
                    features,
                    caretPos,
                    caretPos - toReplace.length()));
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
