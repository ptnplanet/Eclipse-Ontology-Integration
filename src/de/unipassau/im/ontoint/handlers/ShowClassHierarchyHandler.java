package de.unipassau.im.ontoint.handlers;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.unipassau.im.ontoint.OntointLog;
import de.unipassau.im.ontoint.model.WrappedOWLOntology;
import de.unipassau.im.ontoint.views.ClassHierarchyView;


public final class ShowClassHierarchyHandler implements IHandler {

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
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        if (window == null) {
            return null;
        }

        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return null;
        }

        try {
            page.showView(ClassHierarchyView.ID);
        } catch (PartInitException e) {
            OntointLog.logError(e);
            return null;
        }

        IViewPart view = page.findView(ClassHierarchyView.ID);
        if (view == null) {
            return null;
        }

        if (selection instanceof IStructuredSelection) {
            final Object[] selected =
                    ((IStructuredSelection) selection).toArray();
            if ((selected.length > 0)
                    && (selected[0] instanceof WrappedOWLOntology)) {
                ((ClassHierarchyView) view).show(
                        (WrappedOWLOntology) selected[0]);
            }
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
