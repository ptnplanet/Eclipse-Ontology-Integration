package de.unipassau.im.ontoint.importWizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public final class OntologyImportWizardPage extends WizardPage {

    /**
     * Unique identifier.
     */
    public static final String ID = "de.unipassau.im.ontoint.importWizards."
            + "ontologyImportWizard.mainPage";

    private Button URLSelectButton;
    private Label URLTextFieldLabel;
    private Text sourceURLField;

    private Button fileSelectButton;
    private Label fileTextFieldLabel;
    private Text sourceFileField;
    private Button fileBrowseButton;

    protected OntologyImportWizardPage() {
        super(OntologyImportWizardPage.ID);
        this.setTitle("Import Ontology");
        this.setDescription("Import an ontology from the local filesystem or a remote location.");
    }

    @Override
    public void createControl(final Composite parent) {
        final int columnCount = 3;

        // Create a grid layout with 3 columns.
        Composite container = new Composite(parent, SWT.NULL);
        final GridLayout layout = new GridLayout();
        layout.numColumns = columnCount;
        container.setLayout(layout);
        this.setControl(container);

        this.createURLSelectorControls(container);
        this.createFileSelectorControls(container);

        // Add radio button behavior for the remote URL radio button.
        this.URLSelectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                OntologyImportWizardPage.this.enableURLSelection();
            }
        });

        // Add radio button behavior for the local file radio button.
        this.fileSelectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                OntologyImportWizardPage.this.enableFileSelection();
            }
        });

        this.populate();
        this.updatePageComplete();
    }

    /**
     * Load the settings from the last use of this wizard and populate the
     * text fields.
     */
    private void populate() {
        IDialogSettings settings = this.getWizard().getDialogSettings();
        final String sourceURL = settings.get("sourceURLFieldValue");
        if (sourceURL != null) {
            this.sourceURLField.setText(sourceURL);
        }

        final String sourceFile = settings.get("sourceFileFieldValue");
        if (sourceFile != null) {
            this.sourceFileField.setText(sourceFile);
        }

        final String lastSourceSelected = settings.get("sourceSelected");
        if (!"file".equals(lastSourceSelected)) {
            this.enableURLSelection();
        } else {
            this.enableFileSelection();
        }
    }

    /**
     * Enable the URL selection by deactivating the file selection.
     */
    private void enableURLSelection() {
        this.URLSelectButton.setSelection(true);
        this.URLTextFieldLabel.setEnabled(true);
        this.sourceURLField.setEnabled(true);

        this.fileSelectButton.setSelection(false);
        this.fileTextFieldLabel.setEnabled(false);
        this.sourceFileField.setEnabled(false);
        this.fileBrowseButton.setEnabled(false);

        this.sourceURLField.setFocus();
    }

    /**
     * Enable the file selection by deactivating the URL selection.
     */
    private void enableFileSelection() {
        this.fileSelectButton.setSelection(true);
        this.fileTextFieldLabel.setEnabled(true);
        this.sourceFileField.setEnabled(true);
        this.fileBrowseButton.setEnabled(true);

        this.URLSelectButton.setSelection(false);
        this.URLTextFieldLabel.setEnabled(false);
        this.sourceURLField.setEnabled(false);

        this.sourceFileField.setFocus();
    }

    /**
     * Create the controls used for specifying a source URL.
     *
     * @param container The container to add the controls to.
     */
    private void createURLSelectorControls(final Composite container) {
        final int columnCount = 3;

        // GridData object for column span
        final GridData gridSpanData = new GridData();
        gridSpanData.horizontalSpan = columnCount;

        // GridData object for right aligning
        final GridData gridAlignData =
                new GridData(GridData.HORIZONTAL_ALIGN_END);

        // The remote-location radio button
        this.URLSelectButton = new Button(container, SWT.RADIO);
        this.URLSelectButton.setLayoutData(gridSpanData);
        this.URLSelectButton.setText(
                "Select a URL to load an ontology from.");

        // The local-text label
        this.URLTextFieldLabel = new Label(container, SWT.NONE);
        this.URLTextFieldLabel.setLayoutData(gridAlignData);
        this.URLTextFieldLabel.setText("Source URL:");

        // The local-text text field
        this.sourceURLField = new Text(container, SWT.BORDER);
        this.sourceURLField.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                OntologyImportWizardPage.this.updatePageComplete();
            }
        });
        this.sourceURLField.setLayoutData(
                new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create the controls used for specifying a source file.
     *
     * @param container The container to add the controls to.
     */
    private void createFileSelectorControls(final Composite container) {
        final int columnCount = 3;

        // GridData object for column span
        final GridData gridSpanData = new GridData();
        gridSpanData.horizontalSpan = columnCount;

        // GridData object for right aligning
        final GridData gridAlignData =
                new GridData(GridData.HORIZONTAL_ALIGN_END);

        // The local-filesystem radio button
        this.fileSelectButton = new Button(container, SWT.RADIO);
        this.fileSelectButton.setLayoutData(gridSpanData);
        this.fileSelectButton.setText(
                "Select a file to load an ontology from.");

        // The local-text label
        this.fileTextFieldLabel = new Label(container, SWT.NONE);
        this.fileTextFieldLabel.setLayoutData(gridAlignData);
        this.fileTextFieldLabel.setText("Source File:");

        // The local-text text field
        this.sourceFileField = new Text(container, SWT.BORDER);
        this.sourceFileField.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                OntologyImportWizardPage.this.updatePageComplete();
            }
        });
        this.sourceFileField.setLayoutData(
                new GridData(GridData.FILL_HORIZONTAL));

        // The local browse button
        this.fileBrowseButton = new Button(container, SWT.NONE);
        this.fileBrowseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                OntologyImportWizardPage.this.browseForSourceFile();
            }
        });
        this.fileBrowseButton.setText("Browse ...");
    }

    /**
     * Open a file dialog that lets the user browse for a source file to load.
     */
    protected void browseForSourceFile() {
        IPath path = this.getSourcePath();
        if (path == null) {
            return;
        }

        // Open the file dialog for the user to select the source file.
        FileDialog dialog = new FileDialog(this.getShell(), SWT.OPEN);
        if (path != null) {
            if (path.segmentCount() > 1) {
                dialog.setFilterPath(path.removeLastSegments(1).toOSString());
            }
            if (path.segmentCount() > 0) {
                dialog.setFileName(path.lastSegment());
            }
        }
        String result = dialog.open();
        if (result == null) {
            return;
        }
        path = new Path(result);
        this.sourceFileField.setText(path.toString());
    }

    /**
     * Retrieve the source file path from the source file field.
     *
     * @return The path of the source file specified, <code>null</code> if no
     *  file was specified.
     */
    public IPath getSourcePath() {
        String text = this.sourceFileField.getText().trim();
        if (text.length() == 0) {
            return null;
        }
        return new Path(text);
    }

    /**
     * Retrieve the source URL from the source URL field.
     *
     * @return The URL of the source file specified, <code>null</code> if no
     *  URL was specified.
     */
    public String getSourceURL() {
        return "";
    }

    /**
     * Update the page status.
     */
    private void updatePageComplete() {
        this.setPageComplete(false);
    }
}
