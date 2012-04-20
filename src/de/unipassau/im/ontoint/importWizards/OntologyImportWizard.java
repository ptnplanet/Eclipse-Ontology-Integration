package de.unipassau.im.ontoint.importWizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.unipassau.im.ontoint.OntointActivator;

/**
 * The import ontology wizard dialog.
 */
public class OntologyImportWizard extends Wizard implements IImportWizard {

    /**
     * Unique identifier.
     */
    public static final String ID =
            "de.unipassau.im.ontoint.importWizards.ontologyImportWizard";

    /**
     * The main wizard page.
     */
    private OntologyImportWizardPage mainPage;

    /**
     * Creates a new ontology import wizard.  The wizard will try to load
     * any dialog settings in order to save time for the user.
     */
    public OntologyImportWizard() {
        IDialogSettings dialogSettings =
                OntointActivator.getDefault().getDialogSettings();

        // Retrieve the wizards settings or create an empty settings section
        // for this wizard.
        IDialogSettings wizardSettings =
                dialogSettings.getSection(OntologyImportWizard.ID);
        if (wizardSettings == null) {
            wizardSettings =
                    dialogSettings.addNewSection(OntologyImportWizard.ID);
        }
        this.setDialogSettings(dialogSettings);

        this.setNeedsProgressMonitor(true);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean performFinish() {
        final boolean loadFromURL = this.mainPage.isURLSource();
        final URL sourceURL = this.mainPage.getSourceURL();
        final IPath sourceFile = this.mainPage.getSourcePath();

        try {

            this.getContainer().run(true, true, new IRunnableWithProgress() {

                public void run(final IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {

                    monitor.beginTask("Importing Ontology", 10);
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        if (monitor.isCanceled()) {
                            throw new InterruptedException("Canceled by user");
                        }
                        monitor.worked(1);
                    }
                    monitor.done();
                }
            });
        } catch (InvocationTargetException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public final void init(final IWorkbench workbench,
            final IStructuredSelection selection) { }

    /**
     * {@inheritDoc}
     */
    public final void addPages() {
        this.mainPage = new OntologyImportWizardPage();
        this.addPage(this.mainPage);
    }

}
