package de.unipassau.im.ontoint.proposalComputer;

import org.eclipse.core.runtime.Assert;
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
        ICompletionProposalExtension6, Comparable<WrappedOWLEntityProposal> {

    private int currentOffset;

    private int replacementOffset;

    private WrappedOWLEntity proposal;

    public WrappedOWLEntityProposal(final WrappedOWLEntity entity,
            final int caretPosition, final int contextStart) {
        Assert.isNotNull(entity);
        Assert.isTrue(caretPosition >= 0);
        Assert.isTrue(contextStart >= 0);

        this.proposal = entity;
        this.currentOffset = caretPosition;
        this.replacementOffset = contextStart;
    }

    /**
     * {@inheritDoc}
     */
    public StyledString getStyledDisplayString() {
        final StyledString toReturn =
                new StyledString(this.proposal.getShortID());
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
    }

    /**
     * {@inheritDoc}
     */
    public Point getSelection(final IDocument document) {
        return new Point(this.replacementOffset + this.currentOffset, 0);
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
     * Calculates the relevance of this proposal with the given context's
     * features.
     *
     * @return the relevance ranging between 0.0 to 1.0.
     */
    public float calculateRelevance() {
        return 0.0f;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(final WrappedOWLEntityProposal o) {
        if (o == null)
            return 1;

        return Float.compare(this.calculateRelevance(), o.calculateRelevance());
    }

}
