package de.unipassau.im.ontoint.proposals;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
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
     * The default number of features in front or behind the current
     * invocation context to retrieve.
     */
    public static final int DEFAULT_FEATURES_INFRONT = 3;
    public static final int DEFAULT_FEATURES_BEHIND = 2;

    /**
     * Extracts a collection of features from the given context.
     *
     * @param context the context
     * @return the featureset
     */
    public static Collection<ContextFeature> getFeatures(
            final ContentAssistInvocationContext context) {
        return CompletionProposalComputer.getFeatures(
                context.getInvocationOffset(),
                context.getDocument().get(),
                CompletionProposalComputer.DEFAULT_FEATURES_INFRONT,
                CompletionProposalComputer.DEFAULT_FEATURES_BEHIND);
    }

    /**
     * Extracts a collection of features from the given context.
     *
     * @param caretPos the invocation context offset
     * @param context the context
     * @param before the number of features in front of the invocation offset
     * @param after the number of features behind the invocation offset
     * @return the featureset
     */
    public static Collection<ContextFeature> getFeatures(
            final int caretPos,
            final String context,
            final int before, final int after) {
        Assert.isTrue(before >= 0);
        Assert.isTrue(after >= 0);

        String[] features = new String[before + after];
        int j = 0;
        try {
            final String[] tokensInFront = context
                    .substring(0, caretPos).split("\\W+");
            final String[] tokensBehind = context
                    .substring(caretPos, (context.length() - caretPos))
                    .split("\\W+");
            int leftBorder =
                    Math.max(tokensInFront.length - before, 0);
            int rightBorder =
                    Math.max(Math.min(after, tokensBehind.length - 1), 0);
            for (int i = tokensInFront.length - 1; i > leftBorder; i--, j++) {
                features[j] = tokensInFront[i];
            }
            for (int i = 0; i <= rightBorder; i++, j++) {
                features[j] = tokensBehind[i];
            }
        } catch (IndexOutOfBoundsException e) {
            features = new String[0];
        }

        Collection<ContextFeature> toReturn = new LinkedList<ContextFeature>();
        for (String feature : features) {
            if (feature != null) {
                ContextFeature toAdd = new ContextFeature(
                        ContextFeature.Feature.ENVIRONMENTSTRING, feature);
                toReturn.add(toAdd);
            }
        }
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
