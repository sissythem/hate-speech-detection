package gr.di.hatespeech.repositories;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.utils.Utils;
import org.apache.commons.collections.CollectionUtils;

public class FeatureRepository extends AbstractRepository<Feature> {
	private EntityManager em;
	
	public FeatureRepository() {
		super(Utils.FEATURE_TABLE);
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();
	}
	
	public void addFeature(Feature feature) {
		add(em,feature);
	}
	
	public void updateFeature(Feature feature) {
		update(em,feature);
	}
	
	public void removeFeature(Feature feature) {
		remove(em,feature);
	}
	
	public void addFeatures(List<Feature> features) {
		add(em,features);
	}
	
	public void updateFeatures(List<Feature> features) {
		update(em,features);
	}
	
	public void removeFeatures(List<Feature> features) {
		remove(em,features);
	}
	
	public List<Feature> findAllFeatures() {
		return namedQuery(em, Utils.FEATURE_FIND_ALL, null);
	}
	
	public Feature findFeatureById(Long id) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);
		return (Feature)getSingleResult(em, Utils.FEATURE_FIND_BY_ID, parameters);
	}
	
	public List<Feature> findFeatureByKind(String kind) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("kind", kind);
		return namedQuery(em, Utils.FEATURE_FIND_BY_KIND, parameters);
	}
	
	public Feature findFeatureByDescription(String description) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("description", description);
		List<Feature> features = namedQuery(em, Utils.FEATURE_FIND_BY_DESCRIPTION, parameters);
		if(!CollectionUtils.isEmpty(features)) {
			return features.get(0);
		} else {
			return null;
		}
	}
	
}
