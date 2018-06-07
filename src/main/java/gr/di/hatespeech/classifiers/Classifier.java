package gr.di.hatespeech.classifiers;

import gr.di.hatespeech.entities.ClassificationEvaluation;
import weka.core.Instances;

/**
 * This is an interface representing a classifier
 * with a classify method. Gets an Instances object as input
 * and returns a ClassificationResponse object
 * @author sissy
 */
public interface Classifier {
	ClassificationEvaluation crossValidate(Instances trainingInstances);
	ClassificationEvaluation classify(Instances trainingInstances, Instances testInstances);
	void writeResultToFile();
}
