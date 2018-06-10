package gr.di.hatespeech.dataexporters;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Utils;

/**
 * Class to export TextFeatures object to a database or to a CSV
 * @author sissy
 */
public class FeatureExporter extends AbstractDataExporter<Feature> {
	
	public FeatureExporter() {
		super();
	}
	
	@Override
	public void exportDataToCsv(List<Feature> data, String[] headerRecord, String fileName, CsvOptions options) {
		
	}

	public void exportDataToDatabase() {
		TextRepository textRepo = new TextRepository();
		List<Text> texts = textRepo.findAllTexts().stream().limit(1).collect(Collectors.toList());
		Text text = texts.get(0);
		Map<String, Double> f = getVectorFeatures(text);
		Set<String> features = f.keySet();
		factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		features.forEach(feature -> {
			Feature feat = new Feature();
			feat.setDescription(feature);
			addKind(feat);
			em.getTransaction().begin();
			em.persist(feat);
			em.getTransaction().commit();
		});
		em.close();
		factory.close();
	}
	
	private void addKind(Feature feature) {
		if(feature.getDescription().contains(Utils.BOW_KEY_PREFIX)) {
			feature.setKind("bow");
		} else if(feature.getDescription().contains(Utils.CHAR_NGRAM_KEY_PREFIX)) {
			feature.setKind("charngram");
		} else if(feature.getDescription().contains(Utils.NGRAM_KEY_PREFIX)) {
			feature.setKind("ngram");
		} else if(feature.getDescription().contains(Utils.SPELLING_KEY_PREFIX)) {
			feature.setKind("spelling");
		} else if(feature.getDescription().contains(Utils.SYNTAX_KEY_PREFIX)) {
			feature.setKind("syntax");
		} else if(feature.getDescription().contains(Utils.SENTIMENT_KEY_PREFIX)) {
			feature.setKind("sentiment");
		} else if(feature.getDescription().contains(Utils.WORD2VEC_KEY_PREFIX)) {
			feature.setKind("word2vec");
		}
		
	}
	
}
