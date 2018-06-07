package gr.di.hatespeech.classifiers;

import gr.di.hatespeech.entities.ClassificationEvaluation;
import weka.core.Instances;

public abstract class BaseClassifier implements Classifier {
	
	protected ClassificationEvaluation evaluation;
	
	protected int folds = 10;
	protected int runs = 30;
	
	public BaseClassifier(String classifierName) {
		super();
		initEvaluation(classifierName);
	}
	
	public void initEvaluation(String classifierName) {
		evaluation = new ClassificationEvaluation();
		evaluation.setClassifierName(classifierName);
	}
	
	@Override
	public ClassificationEvaluation crossValidate(Instances trainingInstances) {
		return null;
	}

	@Override
	public ClassificationEvaluation classify(Instances trainingInstances, Instances testInstances) {
		return null;
	}
	
	@Override
	public void writeResultToFile() {
		
	}

	public ClassificationEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(ClassificationEvaluation evaluation) {
		this.evaluation = evaluation;
	}

}
