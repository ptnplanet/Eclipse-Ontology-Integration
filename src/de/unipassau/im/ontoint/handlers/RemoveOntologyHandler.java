package de.unipassau.im.ontoint.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.unipassau.im.ontoint.OntointActivator;

/**
 * Handles removing ontologies from the model.
 */
public final class RemoveOntologyHandler implements IHandler {

    /**
     * {@inheritDoc}
     */
    public void addHandlerListener(
            final IHandlerListener handlerListener) { }

    /**
     * {@inheritDoc}
     */
    public void removeHandlerListener(
            final IHandlerListener handlerListener) { }

    /**
     * {@inheritDoc}
     */
    public void dispose() { }

    /**
     * {@inheritDoc}
     */
    public Object execute(final ExecutionEvent event)
            throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            OntointActivator.getDefault().getManager().removeOntologies(
                    ((IStructuredSelection) selection).toArray());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isHandled() {
        return true;
    }

}
