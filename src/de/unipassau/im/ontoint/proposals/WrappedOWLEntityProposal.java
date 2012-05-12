package de.unipassau.im.ontoint.proposals;

import java.util.Collection;

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

/**
 * {@link WrappedOWLEntity} are wrapped within instances of this class
 * implementing the eclipse template proposal interfaces.
 *
 * @author Philipp Nolte
 */
public final class WrappedOWLEntityProposal implements ICompletionProposal,
        IJavaCompletionProposal,
        ICompletionProposalExtension6 {

    /**
     * The classifier to use for relevance calculation.
     */
    private Classifier<ContextFeature, String> classifier;

    /**
     * The current offset (or caret offset) within the document.
     */
    private int currentOffset;

    /**
     * The offset of the string to be replaced.
     */
    private int replacementOffset;

    /**
     * The String to display.
     */
    private String displayString;

    /**
     * The String to actually insert.
     */
    private String replaceString;

    /**
     * The type of this proposal.
     */
    private WrappedOWLEntity.Types type;

    /**
     * The set of features.
     */
    private Collection<ContextFeature> featureset;

    /**
     * Constructs a new proposal with the information and context given.
     *
     * @param entity the entity to propose
     * @param cls the classifier to use for relevance calculation
     * @param features the set of features of the enclosing context
     * @param caretPosition the current caret position in the document
     * @param contextStart the offset of the replacement string
     */
    public WrappedOWLEntityProposal(final WrappedOWLEntity entity,
            final Classifier<ContextFeature, String> cls,
            final Collection<ContextFeature> features,
            final int caretPosition, final int contextStart) {
        Assert.isNotNull(entity);
        Assert.isTrue(caretPosition >= 0);
        Assert.isTrue(contextStart >= 0);

        this.displayString = entity.getShortID();
        this.replaceString = entity.getID();
        this.type = entity.getType();
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
                new StyledString(this.displayString);
        toReturn.append(" [".concat(
                    Integer.toString(this.getRelevance())).concat("] "),
                StyledString.COUNTER_STYLER);
        toReturn.append(" - ".concat(this.replaceString),
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
                    this.replaceString);

            // Learn the user selection
            this.classifier.learn(this.replaceString, this.featureset);
        } catch (BadLocationException e) {
            // ignore
        }
    }

    /**
     * {@inheritDoc}
     */
    public Point getSelection(final IDocument document) {
        return new Point(
                this.currentOffset + this.replaceString.length(), 0);
    }

    /**
     * {@inheritDoc}
     */
    public String getAdditionalProposalInfo() {
        return this.replaceString;
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
                "icons/" + this.type.toString().toLowerCase()
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
        if ((this.classifier == null) || (this.featureset == null))
            return 0;
        return Math.round(this.classifier.classifyDetailed(this.featureset)
                .getProbabilityFor(this.replaceString) * 100.0f);
    }

}

