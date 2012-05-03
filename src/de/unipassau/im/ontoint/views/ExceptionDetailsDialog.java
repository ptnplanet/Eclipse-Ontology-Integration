package de.unipassau.im.ontoint.views;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Dictionary;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

/**
 * A details dialog implementation displaying exception message and stack trace.
 */
public class ExceptionDetailsDialog extends AbstractDetailsDialog {

    /**
     * Detail object to show in details area.
     */
    private final Object details;

    /**
     * Plugin/bundle to show information for.
     */
    private final Bundle bundle;

    /**
     * Constructs a new exception dialog with details area for exception
     * stacktrace and message.
     *
     * @param parentShell the window this dialog is associated to
     * @param dialogTitle the dialogs title
     * @param dialogImage the dialogs image to display
     * @param dialogMessage the message to display
     * @param dialogDetails the details object (exception) to display
     * @param dialogBundle the bundle/plugin
     */
    public ExceptionDetailsDialog(final Shell parentShell,
            final String dialogTitle, final Image dialogImage,
            final String dialogMessage, final Object dialogDetails,
            final Bundle dialogBundle) {

        this(new SameShellProvider(parentShell), dialogTitle, dialogImage,
                dialogMessage, dialogDetails, dialogBundle);
    }

    /**
     * Constructs a new exception dialog with details area for exception
     * stacktrace and message.
     *
     * @param parentShell provider of the window this dialog is associated to
     * @param dialogTitle the dialogs title
     * @param dialogImage the dialogs image to display
     * @param dialogMessage the message to display
     * @param dialogDetails the details object (exception) to display
     * @param dialogBundle the bundle/plugin
     */
    public ExceptionDetailsDialog(final IShellProvider parentShell,
            final String dialogTitle, final Image dialogImage,
            final String dialogMessage, final Object dialogDetails,
            final Bundle dialogBundle) {

        super(parentShell,
                ExceptionDetailsDialog.getTitle(dialogTitle, dialogDetails),
                ExceptionDetailsDialog.getImage(dialogImage, dialogDetails),
                ExceptionDetailsDialog.getMessage(dialogMessage,
                        dialogDetails));
        this.details = dialogDetails;
        this.bundle = dialogBundle;
    }

    /**
     * Returns the dialog title or if it is <code>null</code> creates a title
     * depending on the details object provided.
     *
     * @param title the title to use or <code>null</code> if a title should be
     *   created
     * @param details the details object for title creating
     * @return the title
     */
    public static String getTitle(final String title, final Object details) {
        if (title != null) {
            return title;
        }
        if (details instanceof Throwable) {
            Throwable e = (Throwable) details;
            while (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException) e).getTargetException();
            }
            String name = e.getClass().getName();
            return name.substring(name.lastIndexOf('.' + 1));
        }
        return "Exception";
    }

    /**
     * Returns the dialog image or if it is <code>null</code> creates an image
     * depending on the details object provided.
     *
     * @param image the image to use or <code>null</code> if an image should be
     *   created
     * @param details the details object for image creating
     * @return the image
     */
    public static Image getImage(final Image image, final Object details) {
        if (image != null) {
            return image;
        }
        Display display = Display.getCurrent();
        if (details instanceof IStatus) {
            switch (((IStatus) details).getSeverity()) {
            case IStatus.ERROR:
                return display.getSystemImage(SWT.ICON_ERROR);
            case IStatus.WARNING:
                return display.getSystemImage(SWT.ICON_WARNING);
            case IStatus.INFO:
                return display.getSystemImage(SWT.ICON_INFORMATION);
            default:
                return null;
            }
        }
        return display.getSystemImage(SWT.ICON_ERROR);
    }

    /**
     * Returns the dialog message or if it is <code>null</code> creates a
     * message depending on the details object provided.
     *
     * @param message the message to use or <code>null</code> if an message
     *   should be created
     * @param details the details object for image creating
     * @return the message
     */
    public static String getMessage(final String message,
            final Object details) {

        if (details instanceof Throwable) {
            Throwable e = (Throwable) details;

            // Get the beginning of the invocationTargetException chain
            while (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException) e).getTargetException();
            }
            if (message == null) {
                return e.toString();
            }
            return MessageFormat.format(message,
                    new Object[] {e.toString()});
        }
        if (details instanceof IStatus) {
            String statusMessage = ((IStatus) details).getMessage();
            if (message == null) {
                return statusMessage;
            }
            return MessageFormat.format(message,
                    new Object[] {statusMessage});
        }
        if (message != null) {
            return message;
        }
        return "An exception occured.";
    }

    /**
     * Appends an exception to the dialog output.
     *
     * @param writer the writer to write to
     * @param e the exception
     */
    public static void appendException(final PrintWriter writer,
            final Throwable e) {
        if (e instanceof CoreException) {
            ExceptionDetailsDialog.appendStatus(writer,
                    ((CoreException) e).getStatus(), 0);
        }
        ExceptionDetailsDialog.appendStackTrace(writer, e);
        if (e instanceof InvocationTargetException) {
            ExceptionDetailsDialog.appendException(writer,
                    ((InvocationTargetException) e).getTargetException());
        }
    }

    /**
     * Appends a status object to the dialog's output.
     *
     * @param writer the writer to write to
     * @param status the status to write
     * @param nesting the nested depth
     */
    public static void appendStatus(final PrintWriter writer,
            final IStatus status, final int nesting) {
        final String indent = "  ";
        for (int i = 0; i < nesting; i++) {
            writer.print(indent);
        }

        writer.print(status.getMessage());
        IStatus[] children = status.getChildren();
        for (int i = 0; i < children.length; i++) {
            ExceptionDetailsDialog.appendStatus(writer, children[i],
                    nesting + 1);
        }
    }

    /**
     * Appends an stacktrace to the dialog's output.
     *
     * @param writer the writer to write to
     * @param e the exception which stacktrace to print
     */
    public static void appendStackTrace(final PrintWriter writer,
            final Throwable e) {
        e.printStackTrace(writer);
    }

    /**
     * {@inheritDoc}
     */
    protected final Control createDetailsArea(final Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        this.createProductInfoArea(panel);
        this.createDetailsViewer(panel);

        return panel;
    }

    /**
     * Creates a product info area displaying the bundle information.
     *
     * @param parent the parent composite
     * @return the parent composite
     */
    protected final Composite createProductInfoArea(final Composite parent) {

        if (this.bundle == null) {
            return null;
        }

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayoutData(new GridData());
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = this.convertHorizontalDLUsToPixels(
                IDialogConstants.HORIZONTAL_MARGIN);
        composite.setLayout(layout);

        // Gather Data from bundle manifest.
        Dictionary<?, ?> bundleHeaders = this.bundle.getHeaders();
        String pluginId = this.bundle.getSymbolicName();
        String pluginVendor = (String) bundleHeaders.get("Bundle-Vendor");
        String pluginName = (String) bundleHeaders.get("Bundle-Name");
        String pluginVersion = (String) bundleHeaders.get("Bundle-Version");

        new Label(composite, SWT.NONE).setText("Provider:");
        new Label(composite, SWT.NONE).setText(pluginVendor);
        new Label(composite, SWT.NONE).setText("Plug-In Name:");
        new Label(composite, SWT.NONE).setText(pluginName);
        new Label(composite, SWT.NONE).setText("Plug-In ID:");
        new Label(composite, SWT.NONE).setText(pluginId);
        new Label(composite, SWT.NONE).setText("Version:");
        new Label(composite, SWT.NONE).setText(pluginVersion);

        return composite;
    }

    /**
     * Creates the details viewer -- a read only text area.
     *
     * @param parent the parent composite
     * @return the parent composite
     */
    protected final Control createDetailsViewer(final Composite parent) {
        if (this.details == null) {
            return null;
        }

        Text text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER
                | SWT.H_SCROLL | SWT.V_SCROLL);
        text.setLayoutData(new GridData(GridData.FILL_BOTH));

        final int initialBufferSize = 1000;
        StringWriter writer = new StringWriter(initialBufferSize);
        if (this.details instanceof Throwable) {
            ExceptionDetailsDialog.appendException(new PrintWriter(writer),
                    (Throwable) this.details);
        } else if (this.details instanceof IStatus) {
            ExceptionDetailsDialog.appendStatus(new PrintWriter(writer),
                    (IStatus) this.details, 0);
        }
        text.append(writer.toString());

        return text;
    }


}
