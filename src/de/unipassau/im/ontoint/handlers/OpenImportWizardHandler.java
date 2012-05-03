package de.unipassau.im.ontoint.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import de.unipassau.im.ontoint.importWizards.OntologyImportWizard;

/**
 * Basic command handler, that opens the import ontology wizard.
 */
public class OpenImportWizardHandler implements IHandler {

    /**
     * This handler's id.
     */
    public static final String ID =
            "de.unipassau.im.ontoint.commands.openImportWizard";

    /**
     * {@inheritDoc}
     */
    public final void addHandlerListener(
            final IHandlerListener handlerListener) {
    }

    /**
     * {@inheritDoc}
     */
    public final void removeHandlerListener(
            final IHandlerListener handlerListener) {
    }

    /**
     * {@inheritDoc}
     */
    public final void dispose() {
    }

    /**
     * {@inheritDoc}
     */
    public final Object execute(final ExecutionEvent event)
            throws ExecutionException {

        IWorkbenchWindow window = HandlerUtil
                .getActiveWorkbenchWindowChecked(event);

        OntologyImportWizard wizard = new OntologyImportWizard();
        wizard.init(window.getWorkbench(), StructuredSelection.EMPTY);
        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.open();

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isHandled() {
        return true;
    }

}
