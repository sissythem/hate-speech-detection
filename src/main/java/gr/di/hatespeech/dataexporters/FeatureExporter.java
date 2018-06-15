package gr.di.hatespeech.dataexporters;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.repositories.FeatureRepository;
import gr.di.hatespeech.repositories.TextFeatureRepository;
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
		features.forEach(feature -> exportFeatureToDatabase(em, feature));
		em.close();
		factory.close();
	}

	public void exportFeatureToDatabase(EntityManager em, String feature) {
		Feature feat = new Feature();
		feat.setDescription(feature);
		addKind(feat);
		em.getTransaction().begin();
		em.persist(feat);
		em.getTransaction().commit();
	}

	public void addKind(Feature feature) {
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

	public void deleteNgramsFromDatabase() {
		FeatureRepository featureRepo = new FeatureRepository();
		TextFeatureRepository textFeatureRepository = new TextFeatureRepository();
		List<Feature> feats = featureRepo.findFeatureByKind("ngram");
		feats.addAll(featureRepo.findFeatureByKind("charngram"));
		factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		feats.forEach(feature -> {
			List<TextFeature> tfs = textFeatureRepository.findTextFeatureByFeature(feature.getId());
			tfs.forEach(tf -> {
				Utils.FILE_LOGGER.info("Deleting text feature: " + tf.getId());
				em.getTransaction().begin();
				tf = em.merge(tf);
				em.remove(tf);
				em.getTransaction().commit();
			});
			Utils.FILE_LOGGER.info("Deleting feature" + feature.getDescription());
			em.getTransaction().begin();
			feature = em.merge(feature);
			em.remove(feature);
			em.getTransaction().commit();
		});
		em.close();
		factory.close();
	}
}
