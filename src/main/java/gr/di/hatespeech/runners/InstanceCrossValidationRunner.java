package gr.di.hatespeech.runners;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.features.InstanceGenerator;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Utils;
import weka.core.Instances;

import java.util.List;
import java.util.Properties;

public class InstanceCrossValidationRunner extends InstanceClassificationRunner {
    private static String startingMessageLog = "[" + InstanceCrossValidationRunner.class.getSimpleName() + "] ";

    protected int numFolds;

    public InstanceCrossValidationRunner(int dataset, Properties config, List<Feature> existingFeatures, List<TextFeature> existingTextFeatures, String pathToInstances) {
        super(dataset, config, existingFeatures, existingTextFeatures, pathToInstances);
        this.numFolds = Integer.parseInt(config.getProperty(Utils.NUM_FOLDS));
    }

    public void runCrossValidation() {
        String isNewInstances = config.getProperty(Utils.INSTANCES);
        switch (isNewInstances) {
            case "new":
                generateNewInstances();
                break;
            case "existing":
                readInstancesFromFile();
                break;
            default:
                Utils.FILE_LOGGER.info(startingMessageLog + "Not valid option for instances");
                return;
        }
        ClassificationRunner classificationRunner = new ClassificationRunner(config, pathToInstances, trainingInstances);
        classificationRunner.crossValidate(trainingInstances);
    }

    private void readInstancesFromFile() {
        InstanceGenerator instanceGenerator = new InstanceGenerator();
        trainingInstances = instanceGenerator.readInstancesFromFile(pathToInstances, 0, Utils.TRAIN_INSTANCES_FILE);
        Instances test = instanceGenerator.readInstancesFromFile(pathToInstances, 0, Utils.TEST_INSTACES_FILE);
        for(int i=0;i<test.numInstances(); i++) {
            trainingInstances.add(test.get(i));
        }
    }

    private void generateNewInstances() {
        TextRepository textRepo = new TextRepository();
        List<Text> trainingTexts = textRepo.findAllTexts();
        initGraphFeatureExtractor(trainingTexts, dataset);
        String vectorFeaturesConfig = config.getProperty(Utils.VECTOR_FEATURES);
        trainingTexts.forEach(text -> updateFeaturesList(Utils.TRAIN_INSTANCES_FILE, vectorFeaturesConfig, text));
        trainingInstances = getInstances(trainingFeatures, trainingLabels, Integer.parseInt(config.getProperty(Utils.DATASET)), -1, Utils.TRAIN_INSTANCES_FILE);
    }
}
