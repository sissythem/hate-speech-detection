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
			List<String> lines = getResultLines(evaluation);
			Path file = Paths.get(pathToInstances+foldNumber+"/"+"Result_"+ kind+ "_" +classifierName+".txt");
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeCrossValidateResultsToFile(String pathToInstances, String classifierName) {
		crossValidationEvals.stream().forEach(eval -> {
			try {
				List<String> lines = getResultLines(eval);
				Path file = Paths.get(pathToInstances+"/Result_" + classifierName + ".txt");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private List<String> getResultLines(Evaluation evaluation) throws Exception {
		Double precisionhs = evaluation.precision(0);
		Double precisioncl = evaluation.precision(1);
		Double recallhs = evaluation.recall(0);
		Double recallcl = evaluation.recall(1);
		Double kappa = evaluation.kappa();
		Double fMeasure = evaluation.weightedFMeasure();
		String confusionMatrix = evaluation.toMatrixString("Confusion matrix: ");
		return Arrays.asList("Hate speech precision: " + precisionhs,
				"Non hate speech precision: " + precisioncl, "Hate Speech recall: " + recallhs,
				"Non hate speech recall: " + recallcl, "Kappa: " + kappa.toString(),
				"FMeasure: " + fMeasure.toString(), confusionMatrix, "Summary evaluation: " + evaluation.toSummaryString());
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