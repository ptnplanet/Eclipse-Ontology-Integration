package de.unipassau.im.ontoint.importWizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.unipassau.im.ontoint.Activator;

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
                Activator.getDefault().getDialogSettings();

        // Retrieve the wizards settings or create an empty settings section
        // for this wizard.
        IDialogSettings wizardSettings =
                dialogSettings.getSection(OntologyImportWizard.ID);
        if (wizardSettings == null) {
            wizardSettings =
                    dialogSettings.addNewSection(OntologyImportWizard.ID);
        }

        setDialogSettings(dialogSettings);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean performFinish() {
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
