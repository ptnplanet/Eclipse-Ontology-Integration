package de.unipassau.im.ontoint.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;

public class LoadOntologyRunnable implements IRunnableWithProgress {

    private Collection<URL> URLsToLoad;

    private Collection<File> FilesToLoad;

    public LoadOntologyRunnable() {
        this.URLsToLoad = new HashSet<URL>();
        this.FilesToLoad = new HashSet<File>();
    }

    public boolean addOntologyURL(URL url) {
        return this.URLsToLoad.add(url);
    }

    public boolean addOntologyFile(File file) {
        return this.FilesToLoad.add(file);
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {

        final int numberOfTasks =
                this.URLsToLoad.size() + this.FilesToLoad.size();
        final WrappedOWLOntologyManager manager =
                OntointActivator.getDefault().getManager();

        if (numberOfTasks > 0) {
            monitor.beginTask("Loading ontologies", numberOfTasks);

            // Load from files first.
            for (Iterator<File> it = this.FilesToLoad.iterator();
                    it.hasNext();) {
                File current = it.next();
                monitor.subTask("File " + current.toString());

                

                monitor.worked(1);
            }
        }
        monitor.done();

    }

}
