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
public class OpenImportWizard implements IHandler {

    @Override
    public final void addHandlerListener(
            final IHandlerListener handlerListener) {
    }

    @Override
    public final void dispose() {
    }

    @Override
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

    @Override
    public final boolean isEnabled() {
        return true;
    }

    @Override
    public final boolean isHandled() {
        return true;
    }

    @Override
    public final void removeHandlerListener(
            final IHandlerListener handlerListener) {
    }

}
