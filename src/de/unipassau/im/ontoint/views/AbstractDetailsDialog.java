package de.unipassau.im.ontoint.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog displaying a message and image and providing an area for a more
 * detailed message.
 */
public abstract class AbstractDetailsDialog extends Dialog {

    /**
     * This dialog's title string.
     */
    private final String title;

    /**
     * This dialog's message string.
     */
    private final String message;

    /**
     * This dialog's image to display.
     */
    private final Image image;

    /**
     * The details button that toggles the displaying of the detail area.
     */
    private Button detailsButton;

    /**
     * The details area.
     */
    private Control detailsArea;

    /**
     * The cached window size if the detail area is displayed.
     */
    private Point cachedWindowSize;

    /**
     * Constructs a new dialog associated with the shell and with the title,
     * image and message given.
     *
     * @param parentShell the window this dialog is associated to
     * @param dialogTitle the title of this dialog
     * @param dialogImage the image to display within this dialog
     * @param dialogMessage this dialog's message
     */
    public AbstractDetailsDialog(final Shell parentShell,
            final String dialogTitle, final Image dialogImage,
            final String dialogMessage) {

        this(new SameShellProvider(parentShell), dialogTitle, dialogImage,
                dialogMessage);
    }

    /**
     * Constructs a new dialog associated with the shell and with the title,
     * image and message given.
     *
     * @param parentShell provider of the window this dialog is associated to
     * @param dialogTitle the title of this dialog
     * @param dialogImage the image to display within this dialog
     * @param dialogMessage this dialog's message
     */
    public AbstractDetailsDialog(final IShellProvider parentShell,
            final String dialogTitle,
            final Image dialogImage,
            final String dialogMessage) {

        super(parentShell);
        this.title = dialogTitle;
        this.image = dialogImage;
        this.message = dialogMessage;
        this.setShellStyle(SWT.DIALOG_TRIM | SWT.Resize
                | SWT.APPLICATION_MODAL);
    }

    /**
     * {@inheritDoc}
     */
    protected final void configureShell(final Shell shell) {
        super.configureShell(shell);
        if (this.title != null) {
            shell.setText(this.title);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected final Control createDialogArea(final Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // If an image is present, make room for it and place it in the dialog.
        if (this.image != null) {
            ((GridLayout) composite.getLayout()).numColumns = 2;
            Label label = new Label(composite, 0);
            this.image.setBackground(label.getBackground());
            label.setImage(this.image);
            label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                    | GridData.VERTICAL_ALIGN_BEGINNING));
        }

        // Create a label for this dialog's message.
        Label label = new Label(composite, SWT.WRAP);
        if (this.message != null) {
            label.setText(this.message);
        }
        GridData data = new GridData(GridData.FILL_HORIZONTAL
                | GridData.VERTICAL_ALIGN_CENTER);
        data.widthHint = this.convertHorizontalDLUsToPixels(
                IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        label.setLayoutData(data);
        label.setFont(parent.getFont());

        return composite;
    }

    /**
     * {@inheritDoc}
     */
    protected final void createButtonsForButtonBar(final Composite parent) {
        this.createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);

        // Add the details button.
        this.detailsButton = this.createButton(parent,
                IDialogConstants.DETAILS_ID,
                IDialogConstants.SHOW_DETAILS_LABEL, false);
    }

    /**
     * {@inheritDoc}
     */
    protected final void buttonPressed(final int id) {
        if (id == IDialogConstants.DETAILS_ID) {
            this.toggleDetailsArea();
        } else {
            super.buttonPressed(id);
        }
    }

    /**
     * Toggle the displaying of the details area.
     */
    protected final void toggleDetailsArea() {
        Point oldWindowSize = this.getShell().getSize();
        Point newWindowSize = this.cachedWindowSize;
        cachedWindowSize = oldWindowSize;

        // Change the appearance of the details button.
        if (this.detailsArea == null) {
            this.detailsArea = this.createDetailsArea(
                    (Composite) this.getContents());
            this.detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
        } else {
            this.detailsArea.dispose();
            this.detailsArea = null;
            this.detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
        }

        // Compute new window size.
        Point oldSize = this.getContents().getSize();
        Point newSize = this.getContents().computeSize(
                SWT.DEFAULT, SWT.DEFAULT);
        if (newWindowSize == null) { // Set cache
            newWindowSize = new Point(oldWindowSize.x,
                    oldWindowSize.y + (newSize.y - oldSize.y));
        }

        // Crop to screen
        Point windowLoc = this.getShell().getLocation();
        Rectangle screenArea = this.getContents().getDisplay().getClientArea();
        if (newWindowSize.y > (screenArea.height
               - (windowLoc.y - screenArea.y))) {
            newWindowSize.y = screenArea.height - (windowLoc.y - screenArea.y);
        }

        this.getShell().setSize(newWindowSize);
        ((Composite) this.getContents()).layout();
    }

    /**
     * Creates the details area and its contents.
     *
     * @param parent the parent composite to use
     * @return the detail area control
     */
    protected abstract Control createDetailsArea(Composite parent);

}
