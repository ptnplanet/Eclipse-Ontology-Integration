package de.unipassau.im.ontoint.importWizards;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.semanticweb.owlapi.model.IRI;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.jobs.ImportOntologyFileJob;
import de.unipassau.im.ontoint.jobs.ImportOntologyURLJob;

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
     * This wizards workbench.
     */
    private IWorkbench workbench;

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
        final IRI sourceIRI = this.mainPage.getSourceIRI();
        final IPath sourceFile = this.mainPage.getSourcePath();

        if (loadFromURL && (sourceIRI != null)) {
            new ImportOntologyURLJob("Importing Ontology", sourceIRI)
                .schedule();
        } else if (sourceFile != null) {
            new ImportOntologyFileJob("Importing Ontology", sourceFile.toFile())
                .schedule();
        } else {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public final void init(final IWorkbench workbenchToUse,
            final IStructuredSelection selection) {
        this.workbench = workbench;
    }

    /**
     * {@inheritDoc}
     */
    public final void addPages() {
        this.mainPage = new OntologyImportWizardPage();
        this.addPage(this.mainPage);
    }

}
