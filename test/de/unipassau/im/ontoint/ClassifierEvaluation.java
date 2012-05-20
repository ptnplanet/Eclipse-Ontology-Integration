package de.unipassau.im.ontoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.proposals.Classifier;
import de.unipassau.im.ontoint.proposals.ContextFeature;

public class ClassifierEvaluation {

    private WrappedOWLOntologyManager manager;

    private Classifier<ContextFeature, String> classifier;

    private String[][] szenario;

    @Before
    public void setUp() throws Exception {

        this.manager = new OntointActivator(false).getManager();
        this.loadOntologies();
        this.readSzenario();

    }

    private void loadOntologies() throws Exception {
        final String[] filesToLoad = new String[] {
                "/Library/WebServer/Documents/testszenario/common.owl",
                "/Library/WebServer/Documents/testszenario/documents.owl",
                "/Library/WebServer/Documents/testszenario/keywords.owl",
                "/Library/WebServer/Documents/testszenario/processes.owl",
        };
        for (String fileName : filesToLoad) {
            this.manager.getWrappedManager().loadOntologyFromOntologyDocument(
                    new File(fileName));
        }
    }

    private void readSzenario() throws Exception {
        final StringBuilder text = new StringBuilder();
        final String sep = System.getProperty("line.separator");
        final Scanner scanner = new Scanner(new FileInputStream(
                "/Library/WebServer/Documents/testszenario/dlr3docx.drl"),
                "UTF-8");
        try {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine() + sep);
            }
        } finally {
            scanner.close();
        }

        text.toString().split("%%%%%%%%%%");
    }

    @Test
    public void test() {
        
    }

}
