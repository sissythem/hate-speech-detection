package gr.di.hatespeech.repositories;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;

/**
 * This is an implementation of AbstractRepository for Text objects
 * including all necessary calls to the database
 * @author sissy
 */
public class TextRepository extends AbstractRepository<Text> {
	private EntityManager em;
	
	public TextRepository() {
		super(Utils.TEXT_TABLE);
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();
	}
	
	public void addText(Text text) {
		add(em, text);
	}
	
	public void updateText(Text text) {
		update(em, text);
	}
	
	public void removeText(Text text) {
		remove(em, text);
	}
	
	public void addTexts(List<Text> texts) {
		add(em, texts);
	}
	
	public void updateTexts(List<Text> texts) {
		update(em, texts);
	}
	
	public void removeTexts(List<Text> texts) {
		remove(em, texts);
	}
	
	public List<Text> findAllTexts() {
		return namedQuery(em, Utils.TEXT_FIND_ALL, null);
	}
	
	public Text findTextById(Long id) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);
		return (Text) getSingleResult(em, Utils.TEXT_FIND_BY_ID, parameters);
	}
	
	public List<Text> findTextsByLabel(String label) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("label", label);
		return namedQuery(em, Utils.TEXT_FIND_BY_LABEL, parameters);
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

}
