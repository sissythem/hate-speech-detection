package gr.di.hatespeech.utils;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;
import gr.di.hatespeech.entities.Text;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class GraphUtils {

	private static String startingMessageLog = "[" + GraphUtils.class.getSimpleName() + "] ";
	/**
	 * Gets as input a text and the graph type and generates
	 * a graph for the given text
	 * @param text, text to extract graph
	 * @param type, DocumentNGramGraph type (word or ngram)
	 * @return a new DocumentNGramGraph object for a specific text
	 */
	public static DocumentNGramGraph getTextGraph(Text text, String type) {
		DocumentNGramGraph textGraph = getDocumentNGramGraph(type);
		textGraph.setDataString(text.getPrepMessage());
		return textGraph;
	}

	/**
	 * Generate a new DocumentNGramGraph based on its type
	 * @param type, word or ngram
	 * @return a new DocumentNGramGraph object
	 */
	private static DocumentNGramGraph getDocumentNGramGraph(String type) {
		DocumentNGramGraph textGraph = null;
		if(type.equalsIgnoreCase("ngram")) {
			textGraph = new DocumentNGramGraph();
		} else if(type.equalsIgnoreCase("word")) {
			textGraph = new DocumentWordGraph();
		} else {
			Utils.FILE_LOGGER.error(startingMessageLog + "Invalid graph type was given");
		}
		return textGraph;
	}

	/**
	 * Merge all graph generated from a training sample based on a label
	 * @param graphs, all graphs generated from training instances
	 * @param type, word or ngram graph
	 * @return class graph
	 */
	public static DocumentNGramGraph mergeGraphs(List<DocumentNGramGraph> graphs, String type) {
		DocumentNGramGraph classGraph = getDocumentNGramGraph(type);
		if(!CollectionUtils.isEmpty(graphs)) {
			int i = 0;
			for(DocumentNGramGraph graph : graphs) {
				classGraph.merge(graph, 1.0 / (1.0 + i));
				i++;
			}
		}
		return classGraph;
	}
}
