package de.unipassau.im.ontoint.proposalComputer;

import java.util.Collection;
import java.util.Comparator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.WrappedOWLEntity;

public final class WrappedOWLEntityProposal implements ICompletionProposal,
        IJavaCompletionProposal,
        ICompletionProposalExtension6 {

    private Classifier<ContextFeature, String> classifier;

    private int currentOffset;

    private int replacementOffset;

    private WrappedOWLEntity proposal;

    private Collection<ContextFeature> featureset;

    public WrappedOWLEntityProposal(final WrappedOWLEntity entity,
            final Classifier<ContextFeature, String> cls,
            final Collection<ContextFeature> features,
            final int caretPosition, final int contextStart) {
        Assert.isNotNull(entity);
        Assert.isTrue(caretPosition >= 0);
        Assert.isTrue(contextStart >= 0);

        this.proposal = entity;
        this.classifier = cls;
        this.featureset = features;
        this.currentOffset = caretPosition;
        this.replacementOffset = contextStart;
    }

    /**
     * {@inheritDoc}
     */
    public StyledString getStyledDisplayString() {
        final StyledString toReturn =
                new StyledString(this.proposal.getShortID());
        toReturn.append(" [".concat(Integer.toString(this.getRelevance())).concat("] "), StyledString.COUNTER_STYLER);
        toReturn.append(" - ".concat(this.proposal.getID()),
                StyledString.QUALIFIER_STYLER);
        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final IDocument document) {
        try {
            document.replace(
                    this.replacementOffset,
                    this.currentOffset - this.replacementOffset,
                    this.proposal.getID());
        } catch (BadLocationException e) {
            // ignore
        }

        // Learn the user selection
        this.classifier.learn(this.proposal.getID(), this.featureset);
    }

    /**
     * {@inheritDoc}
     */
    public Point getSelection(final IDocument document) {
        return new Point(
                this.currentOffset + this.proposal.getID().length(), 0);
    }

    /**
     * {@inheritDoc}
     */
    public String getAdditionalProposalInfo() {
        return this.proposal.getID();
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayString() {
        return this.getStyledDisplayString().getString();
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        final String icon =
                "icons/" + this.proposal.getType().toString().toLowerCase()
                + ".gif";
        return OntointActivator.getDefault().getImageCache().getImage(
                OntointActivator.getImageDescriptor(icon));
    }

    /**
     * {@inheritDoc}
     */
    public IContextInformation getContextInformation() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getRelevance() {
        if (this.classifier == null)
            return 0;
        if (this.featureset == null)
            return 0;
        return Math.round(this.classifier.classifyDetailed(this.featureset)
                .getProbabilityFor(this.proposal.getID()) * 100.0f);
    }

}

