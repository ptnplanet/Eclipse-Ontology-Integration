package de.unipassau.im.ontoint;

import hudson.util.EditDistance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.unipassau.im.ontoint.model.WrappedOWLEntity;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.proposals.BayesClassifier;
import de.unipassau.im.ontoint.proposals.Classification;
import de.unipassau.im.ontoint.proposals.ContextFeature;
import de.unipassau.im.ontoint.proposals.DetailedClassification;

public class ClassifierEvaluation {

    private WrappedOWLOntologyManager manager;

    private BayesClassifier<ContextFeature, String> classifier;

    private Map<Integer, String> entityPositions;

    private List<String> stopwords;

    private String[] szenario;

    private static final int MAX_EDIT_DISTANCE = 1;

    private static final int LEARNING_CORPUS_SIZE = 80;

    private static final int CONTEXT_BEFORE = 4;

    private static final int CONTEXT_AFTER = 2;

    @Before
    public void setUp() throws Exception {

        this.manager = new OntointActivator(false).getManager();
        this.classifier = new BayesClassifier<ContextFeature, String>();
        this.entityPositions = new HashMap<Integer, String>();

        this.loadOntologies();
        this.loadStopwords();
        this.createSzenario();
    }

    @Test
    public void test() throws Exception {

        // randomize the test szenario
        List<Integer> randomPositions = Arrays.asList(
                        this.entityPositions.keySet().toArray(
                                new Integer[this.entityPositions.size()]));
        Collections.shuffle(randomPositions);

        // Learning
        int border = (int) ((float) randomPositions.size() / (float) 100
                * (float) ClassifierEvaluation.LEARNING_CORPUS_SIZE);
        for (int i = 0; i < border; i++) {

            int pos = randomPositions.get(i);
            this.classifier.learn(
                    this.entityPositions.get(pos),
                    this.getContextFeatures(pos));
        }

        // Evaluation
        for (int i = border; i < randomPositions.size(); i++) {

            int pos = randomPositions.get(i);
            DetailedClassification<ContextFeature, String> c =
                    this.classifier.classifyDetailed(
                            this.getContextFeatures(pos));

            System.out.println(c.getPositionFor(this.entityPositions.get(pos)) + "\t" + this.entityPositions.get(pos) + "\t" + c.getCategoryAtPosition(1) + "\t" + c.getProbabilityFor(this.entityPositions.get(pos)));
        }

    }

    private void loadOntologies() throws Exception {
        final String[] filesToLoad = new String[] {
                "/Library/WebServer/Documents/Astronomy.owl",
        };
        for (String fileName : filesToLoad) {
            this.manager.getWrappedManager().loadOntologyFromOntologyDocument(
                    new File(fileName));
        }
    }

    private void loadStopwords() throws Exception {
        StringBuffer data = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader("/Users/philipp/Dropbox/Bachelorarbeit IC/Szenario/stopwords.txt"));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            data.append(readData);
            buf = new char[1024];
        }
        reader.close();
        this.stopwords = Arrays.asList(data.toString().split("\\W"));
    }

    private String loadSzenarioFiles() throws Exception {
        final String[] filesToLoad = new String[] {
                "/Users/philipp/Dropbox/Bachelorarbeit IC/Szenario/Astronomy.txt",
                "/Users/philipp/Dropbox/Bachelorarbeit IC/Szenario/Galaxy.txt",
                "/Users/philipp/Dropbox/Bachelorarbeit IC/Szenario/Planet.txt"
        };

        StringBuffer data = new StringBuffer(5000);
        for (String fileName : filesToLoad) {

            BufferedReader reader = new BufferedReader(
                    new FileReader(fileName));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                data.append(readData);
                buf = new char[1024];
            }
            reader.close();
        }
        return data.toString();
    }

    private void createSzenario() throws Exception {
        String data = this.loadSzenarioFiles();

        // Remove any unusable characters from the data
        data = data.replaceAll("[^A-Za-z0-9 _-]", "").toLowerCase();

        List<String> splitData = new ArrayList<String>(Arrays.asList(data.split("\\s")));

        // Go through the array and try to find two words, that match an owl
        // entity
        for (int i = 0; i < splitData.size() - 1; i++) {

            // either one of the words a stopword?
            if (!this.stopwords.contains(splitData.get(i))
                    && !this.stopwords.contains(splitData.get(i + 1))) {
                String toTest = splitData.get(i).concat(splitData.get(i + 1));
                String bestMatch = this.getBestEntityMatch(toTest);
                if (bestMatch != null) {
                    splitData.set(i, toTest);
                    splitData.remove(i + 1);
                    this.entityPositions.put(new Integer(i), bestMatch);
                }
            }
        }

        // Go through the array and try and find single words, that match an
        // owl entity
        for (int i = 0; i < splitData.size() - 1; i++) {
            if ((this.entityPositions.get(new Integer(i)) == null)
                    && !this.stopwords.contains(splitData.get(i))) {
                String toTest = splitData.get(i);
                String bestMatch = this.getBestEntityMatch(toTest);
                if (bestMatch != null) {
                    splitData.set(i, toTest);
                    this.entityPositions.put(new Integer(i), bestMatch);
                }
            }
        }

        this.szenario = splitData.toArray(new String[splitData.size()]);

//        for (Integer i : this.entityPositions.keySet()) {
//            System.out.println(i + "\t" + this.entityPositions.get(i));
//        }
//        System.out.println(this.entityPositions.size());
    }

    private String getBestEntityMatch(String toTest) {
        if (toTest.length() < 1)
            return null;

        Set<WrappedOWLEntity> proposals =
                this.manager.getAutocompleteTemplates("" + toTest.charAt(0));

        int bestScore = Integer.MAX_VALUE;
        String bestMatch = null;
        for (WrappedOWLEntity proposal : proposals) {
            String id = proposal.getShortID().toLowerCase();
            int score = EditDistance.editDistance(id, toTest);
            if ((score < bestScore)
                    && (score <= ClassifierEvaluation.MAX_EDIT_DISTANCE)) {
//                System.out.println(score + "\t" + id + "\t" + toTest);
                bestMatch = id;
                bestScore = score;
            }
        }

        return bestMatch;
    }

    private Collection<ContextFeature> getContextFeatures(int wordPos) {

        int start = Math.max(0, wordPos - ClassifierEvaluation.CONTEXT_BEFORE);
        int end = Math.min(this.szenario.length - 1, wordPos + ClassifierEvaluation.CONTEXT_AFTER);

        Collection<ContextFeature> features = new LinkedList<ContextFeature>();
        for (int i = start; i <= end; i++) {
            if (i != wordPos) {
                features.add(new ContextFeature(
                   ContextFeature.Feature.ENVIRONMENTSTRING, this.szenario[i]));
            }
        }
        return features;
    }

}
