package gr.di.hatespeech.repositories;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.apache.commons.collections4.CollectionUtils;

import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.utils.Utils;

public class TextFeatureRepository extends AbstractRepository<TextFeature> {
	private EntityManager em;
	public TextFeatureRepository() {
		super(Utils.TEXT_FEATURE_TABLE);
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();
	}
	
	public void addTextFeature(TextFeature textFeature) {
		add(em,textFeature);
	}
	
	public void updateTextFeature(TextFeature textFeature) {
		update(em,textFeature);
	}
	
	public void removeTextFeature(TextFeature textFeature) {
		remove(em,textFeature);
	}
	
	public void addTextFeatures(List<TextFeature> textFeatures) {
		add(em,textFeatures);
	}
	
	public void updateTextFeatures(List<TextFeature> textFeatures) {
		update(em,textFeatures);
	}
	
	public void removeTextFeatures(List<TextFeature> textFeatures) {
		remove(em,textFeatures);
	}
	
	public List<TextFeature> findAllTextFeatures() {
		return namedQuery(em, Utils.TEXT_FEATURE_FIND_ALL, null);
	}
	
	public TextFeature findTextFeatureById(Long id) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);
		return (TextFeature)getSingleResult(em, Utils.TEXT_FEATURE_FIND_BY_ID, parameters);
	}
	
	public TextFeature findTextFeatureByTextAndFeature(Long textId, Long featureId) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("textId", textId);
		parameters.put("featureId", featureId);
		List<TextFeature> tf = namedQuery(em, Utils.TEXT_FEATURE_FIND_BY_TEXT_AND_FEATURE, parameters);
		if(!CollectionUtils.isEmpty(tf)) {
			return tf.get(0);
		} else {
			return null;
		}
	}
	
	public List<TextFeature> findTextFeatureByText(Long textId) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("textId", textId);
		return namedQuery(em, Utils.TEXT_FEATURE_FIND_BY_TEXT, parameters);
	}
	
	public List<TextFeature> findTextFeatureByFeature(Long featureId) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("featureId", featureId);
		return namedQuery(em, Utils.TEXT_FEATURE_FIND_BY_FEATURE, parameters);
	}

	public List<TextFeature> findTextFeatureByFeatureKind(String kind) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("kind", kind);
		return namedQuery(em, Utils.TEXT_FEATURE_FIND_BY_FEATURE_KIND, parameters);
	}
}
