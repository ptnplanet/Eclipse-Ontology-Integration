package de.unipassau.im.ontoint;

import hudson.util.EditDistance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import de.unipassau.im.ontoint.model.WrappedOWLEntity;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.proposals.BayesClassifier;
import de.unipassau.im.ontoint.proposals.ContextFeature;
import de.unipassau.im.ontoint.proposals.DetailedClassification;

public class ClassifierEvaluation {

    private WrappedOWLOntologyManager manager;

    private BayesClassifier<ContextFeature, String> classifier;

    private Map<Integer, String> entityPositions;

    private List<String> stopwords;

    private String[] szenario;

    private static final String DIR =
            "/Users/philipp/Dropbox/Bachelorarbeit IC/Szenario/";

    private static final int MAX_EDIT_DISTANCE = 1;

    private static final int LEARNING_CORPUS_SIZE = 30;

    private static final boolean OUTPUT_TO_FILE = false;

    @Before
    public void setUp() throws Exception {

        this.manager = new OntointActivator(false).getManager();
        this.entityPositions = new HashMap<Integer, String>();

        this.loadOntologies();
        this.loadStopwords();
        this.createSzenario();
        this.printEntityDistribution();
    }

    @Test
    public void test() throws Exception {

        final int i = 5;
        final int l = 3;
        final float[] values = new float[(i + 1) * (i + 1)];

        int k = 0;
        for (int before = 0; before <= i; before++) {
            for (int after = 0; after <= i; after++) {

                // For better median values, evaluate multiple times and
                // calculate their average
                float avg = 0.0f;
                for (int j = 0; j < l; j++) {
                    avg += this.singleTest(before, after, j);
                }
                values[k++] = avg / (float) l;
                System.out.println(k + "/" + ((i + 1) * (i + 1)));
            }
        }

        this.printMatrix(i, i, values);

    }

    private void printEntityDistribution() {
        SortedMap<String, Integer> entities = new TreeMap<String, Integer>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
        });

        for (String e : this.entityPositions.values()) {
            if (!entities.containsKey(e))
                entities.put(e, 0);
            entities.put(e, entities.get(e) + 1);
        }

        System.out.println("\nEnity\t#");
        for (Entry<String, Integer> e : entities.entrySet()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }
    }

    private void printMatrix(int x, int y, float[] avg) {
        int k = 0;
        System.out.print("\nbefore/after\t");
        for (int i = 0; i <= y; i++)
            System.out.print(i + "\t");
        for (int i = 0; i <= x; i++) {
            System.out.print("\n" + i + "\t");
            for (int j = 0; j <= y; j++) {
                System.out.print(avg[k++] + "\t");
            }
        }
    }

    private float singleTest(int before, int after, int j) throws Exception {
        this.classifier = new BayesClassifier<ContextFeature, String>();
        this.classifier.setMemoryCapacity(this.entityPositions.size() + 1);

        FileWriter out;
        if (ClassifierEvaluation.OUTPUT_TO_FILE)
            out = new FileWriter(new File(
                    ClassifierEvaluation.DIR + "eval/eval" + "_" + before + "_"
                            + after + "_" + j + ".tsv"));

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
                    this.getContextFeatures(pos, before, after));
        }

        if (ClassifierEvaluation.OUTPUT_TO_FILE)
            out.write("\n" + "Starting with params b=" + before
                    + " a=" + after + "\n");

        if (ClassifierEvaluation.OUTPUT_TO_FILE)
            out.write("Position"
                    + "\t" + "Expected"
                    + "\t" + "Proposed"
                    + "\t" + "ProbabilityOfExpected"
                    + "\t" + "ProbabilityOfProposed");

        // Evaluation
        int[] medianValues = new int[randomPositions.size() - border];
        for (int i = border; i < randomPositions.size(); i++) {

            int pos = randomPositions.get(i);
            Collection<ContextFeature> features =
                    this.getContextFeatures(pos, before, after);

            // classify
            DetailedClassification<ContextFeature, String> c =
                    this.classifier.classifyDetailed(features);
            String entity = this.entityPositions.get(pos);
            String posOne = c.getCategoryAtPosition(1);
            medianValues[i - border] = c.getPositionFor(entity);

            if (ClassifierEvaluation.OUTPUT_TO_FILE)
                out.write("\n" + c.getPositionFor(entity)
                        + "\t" + entity
                        + "\t" + posOne
                        + "\t" + c.getProbabilityFor(entity)
                        + "\t" + c.getProbabilityFor(posOne));

            // learn
            this.classifier.learn(entity, features);
        }

        float median = this.median(medianValues);

        if (ClassifierEvaluation.OUTPUT_TO_FILE)
            out.write("\n" + "Position Median with params b=" + before
                    + " a=" + after
                    + ":\t"
                    + median);

        if (ClassifierEvaluation.OUTPUT_TO_FILE)
            out.close();

        return median;
    }

    private void loadOntologies() throws Exception {
        final String[] filesToLoad = new String[] {
                ClassifierEvaluation.DIR + "Astronomy.owl",
        };
        for (String fileName : filesToLoad) {
            this.manager.getWrappedManager().loadOntologyFromOntologyDocument(
                    new File(fileName));
        }
        System.out.println("Ontology elements: "
                    + this.manager.getAutocompleteTemplates().size());
    }

    private void loadStopwords() throws Exception {
        StringBuffer data = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(ClassifierEvaluation.DIR + "stopwords.txt"));
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
                ClassifierEvaluation.DIR + "Astronomy.txt",
                ClassifierEvaluation.DIR + "Galaxy.txt",
                ClassifierEvaluation.DIR + "Planet.txt",
                ClassifierEvaluation.DIR + "Milky_Way.txt",
                ClassifierEvaluation.DIR + "Star.txt",
                ClassifierEvaluation.DIR + "Comet.txt",
                ClassifierEvaluation.DIR + "Cosmology.txt",
                ClassifierEvaluation.DIR + "Observational_Astronomy.txt",
                ClassifierEvaluation.DIR + "Astrophysics.txt",
                ClassifierEvaluation.DIR + "Open_Cluster.txt",
                ClassifierEvaluation.DIR + "Star_Cluster.txt",
                ClassifierEvaluation.DIR + "Extrasolar_Planet.txt"
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

        List<String> splitData = new ArrayList<String>(Arrays.asList(
                data.split("\\s")));

        System.out.println("Szenario size: " + splitData.size() + " tokens");

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
        System.out.println("Entity tokens in szenario: " + this.entityPositions.size());
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
//            if (id.equals("star"))
//                continue;
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

    private Collection<ContextFeature> getContextFeatures(int wordPos,
            int before, int after) {

        int start = Math.max(0, wordPos - before);
        int end = Math.min(this.szenario.length - 1, wordPos + after);

        Collection<ContextFeature> features = new LinkedList<ContextFeature>();
        for (int i = start; i <= end; i++) {
            if (i != wordPos) {
                features.add(new ContextFeature(
                   ContextFeature.Feature.ENVIRONMENTSTRING, this.szenario[i]));
            }
        }
        return features;
    }

    private float median(int[] values) {
        Arrays.sort(values);
        if ((values.length % 2) != 0)
            return values[values.length / 2];
        return (float) (values[values.length / 2 - 1]
                + values[values.length / 2]) / 2.0f;
    }

}
