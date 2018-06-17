package gr.di.hatespeech.classifiers;

import gr.di.hatespeech.entities.ClassificationEvaluation;
import gr.di.hatespeech.utils.Utils;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import java.util.Random;

public abstract class BaseClassifier implements Classifier {

	private static String startingMessageLog = "[" + BaseClassifier.class.getSimpleName() + "] ";

	protected ClassificationEvaluation evaluation;
	
	protected int folds;
	protected int runs;
	
	public BaseClassifier(String classifierName) {
		super();
		initEvaluation(classifierName);
	}

	public BaseClassifier(String name, int folds, int runs) {
		super();
		this.folds = folds;
		this.runs = runs;
		initEvaluation(name);
	}
	
	public void initEvaluation(String classifierName) {
		evaluation = new ClassificationEvaluation();
		evaluation.setClassifierName(classifierName);
	}

	public void evaluateCrossValidation(Instances trainingInstances, int run, weka.classifiers.Classifier classifier) {
		try{
		Evaluation eval = new Evaluation(trainingInstances);
		evaluation.addCrossValidationEval(eval);
		eval.crossValidateModel(classifier, trainingInstances, folds, new Random(run));
		Double kappa = eval.kappa();
		Double fMeasure = eval.weightedFMeasure(); //average F-Measure
		String confusionMatrix = eval.toMatrixString("Confusion matrix: ");
		Utils.FILE_LOGGER.info(startingMessageLog + "Kappa - " + kappa + ", fMeasure - " + fMeasure + ", confusionMatrix - " + confusionMatrix);
		} catch (Exception e) {
			Utils.FILE_LOGGER.error(e.getMessage(),e);
		}
	}

	public void evaluateClassification(Instances trainingInstances, Instances testInstances, weka.classifiers.Classifier classifier) {

		try {
			Utils.FILE_LOGGER.info(startingMessageLog + "Building classifier");
			classifier.buildClassifier(trainingInstances);
			Evaluation trainEval = new Evaluation(trainingInstances);
			evaluation.setTrainEval(trainEval);
			evaluation.setTrainingPredictions(trainEval.evaluateModel(classifier, trainingInstances));
			for(int instanceIndex =0; instanceIndex < testInstances.numInstances(); instanceIndex++) {
				classifier.classifyInstance(testInstances.get(instanceIndex));
			}
			Utils.FILE_LOGGER.info(startingMessageLog + "Testing classifier");
			Evaluation testEval = new Evaluation(trainingInstances);
			evaluation.setTestPredictions(testEval.evaluateModel(classifier, testInstances));
			evaluation.setTestEval(testEval);
			Double kappa = testEval.kappa();
			Double fMeasure = testEval.weightedFMeasure(); //average F-Measure
			String confusionMatrix = testEval.toMatrixString("Confusion matrix: ");
			Utils.FILE_LOGGER.info(startingMessageLog + "Kappa - " + kappa + ", fMeasure - " + fMeasure + ", confusionMatrix - " + confusionMatrix);
			Utils.FILE_LOGGER.info(startingMessageLog + "Summary: " + testEval.toSummaryString());
		} catch (Exception e) {
			e.printStackTrace();
			Utils.FILE_LOGGER.error(e.getMessage(),e);
		}
	}
	
	@Override
	public ClassificationEvaluation crossValidate(Instances trainingInstances) {
		return null;
	}

	@Override
	public ClassificationEvaluation classify(Instances trainingInstances, Instances testInstances) {
		return null;
	}
	
	public ClassificationEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(ClassificationEvaluation evaluation) {
		this.evaluation = evaluation;
	}

}
