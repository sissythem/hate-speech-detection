package gr.di.hatespeech.entities;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import weka.classifiers.evaluation.Evaluation;

/**
 * Class representing the classification results. Keeps the classifier's name
 * (NaiveBayes, KNN, LogisticRegression) the predictions and the evaluation
 * @author sissy
 */
public class ClassificationEvaluation {
	private String classifierName;
	private Evaluation trainEval;
	private Evaluation testEval;
	private List<Evaluation> crossValidationEvals = new ArrayList<>();
	private double[] testPredictions;
	private double[] trainingPredictions;

	public ClassificationEvaluation() {

	}

	public void writeClassificationEvalToFile(Evaluation evaluation, String pathToInstances, int foldNumber, String kind) {
		try {
			Double precisionhs = evaluation.precision(0);
			Double precisioncl = evaluation.precision(1);
			Double recallhs = evaluation.recall(0);
			Double recallcl = evaluation.recall(1);
			Double kappa = evaluation.kappa();
			Double fMeasure = evaluation.weightedFMeasure();
			String confusionMatrix = evaluation.toMatrixString("Confusion matrix: ");
			List<String> lines = Arrays.asList("Hate speech precision: " + precisionhs,
					"Non hate speech precision: " + precisioncl, "Hate Speech recall: " + recallhs,
					"Non hate speech recall: " + recallcl, "Kappa: " + kappa.toString(),
					"FMeasure: " + fMeasure.toString(), confusionMatrix, "Summary evaluation: " + evaluation.toSummaryString());
			Path file = Paths.get(pathToInstances+foldNumber+"/"+"Result_"+ kind+ "_" +classifierName+".txt");
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeCrossValidateResultsToFile(String pathToInstances, int foldNumber) {
		crossValidationEvals.stream().forEach(eval -> {
			writeClassificationEvalToFile(eval, pathToInstances, foldNumber, "train");
		});
		
	}

	public String getClassifierName() {
		return classifierName;
	}

	public double[] getTestPredictions() {
		return testPredictions;
	}

	public void setTestPredictions(double[] testPredictions) {
		this.testPredictions = testPredictions;
	}

	public double[] getTrainingPredictions() {
		return trainingPredictions;
	}

	public void setTrainingPredictions(double[] trainingPredictions) {
		this.trainingPredictions = trainingPredictions;
	}

	public Evaluation getTrainEval() {
		return trainEval;
	}

	public void setTrainEval(Evaluation trainEval) {
		this.trainEval = trainEval;
	}

	public Evaluation getTestEval() {
		return testEval;
	}

	public void setTestEval(Evaluation testEval) {
		this.testEval = testEval;
	}

	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}

	public List<Evaluation> getCrossValidationEvals() {
		return crossValidationEvals;
	}

	public void setCrossValidationEvals(List<Evaluation> trainEvals) {
		this.crossValidationEvals = trainEvals;
	}

	public void addCrossValidationEval(Evaluation crossValidationEval) {
		if(CollectionUtils.isEmpty(crossValidationEvals)) {
			this.crossValidationEvals = new ArrayList<>();
		}
		this.crossValidationEvals.add(crossValidationEval);
	}
	
	public void removeCrossValidationEval(Evaluation crossValidationEval) {
		if (!CollectionUtils.isEmpty(crossValidationEvals)) {
			this.crossValidationEvals.remove(crossValidationEval);
		}
	}
}