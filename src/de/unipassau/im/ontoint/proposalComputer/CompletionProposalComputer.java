package de.unipassau.im.ontoint.proposalComputer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import de.unipassau.im.ontoint.OntointActivator;

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
        monitor.beginTask("Building proposal list.", IProgressMonitor.UNKNOWN);

        final int caretPos = context.getInvocationOffset();
        final Set<OWLEntity> proposals =
                OntointActivator.getDefault().getManager()
                .getAutocompleteTemplates();
        final List<ICompletionProposal> toReturn =
                new LinkedList<ICompletionProposal>();

        for (OWLEntity proposal : proposals) {
            //toReturn.add(new CompletionProposal(proposal, caretPos,
              //      proposal.length(), caretPos + proposal.length()));
            final String pID = proposal.toStringID();
            toReturn.add(new CompletionProposal(
                    pID,
                    caretPos,
                    0,
                    caretPos + pID.length(),
                    this.getImageFor(proposal),
                    pID.substring(pID.lastIndexOf('#') + 1),
                    null,
                    proposal.getEntityType().toString()));
        }

        monitor.done();
        return toReturn;
    }

    private Image getImageFor(OWLEntity entity) {
        ImageDescriptor desc = null;
        if (entity.isOWLClass()) {
            desc = OntointActivator.getImageDescriptor("icons/class.gif");
        } else if (entity.isOWLNamedIndividual()) {
            desc = OntointActivator.getImageDescriptor("icons/individual.gif");
        } else if (entity.isOWLDataProperty()) {
            desc = OntointActivator.getImageDescriptor("icons/property.gif");
        } else if (entity.isOWLDatatype()) {
            desc = OntointActivator.getImageDescriptor("icons/type.gif");
        }
        if (desc != null) {
            return OntointActivator.getDefault().getImageCache().getImage(desc);
        }
        return null;
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
