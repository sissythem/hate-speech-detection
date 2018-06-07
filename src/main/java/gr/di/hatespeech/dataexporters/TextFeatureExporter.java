package gr.di.hatespeech.dataexporters;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.apache.commons.lang3.StringUtils;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.repositories.FeatureRepository;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Utils;

/**
 * Class to export TextFeatures object to a database or to a CSV
 * @author sissy
 */
public class TextFeatureExporter extends AbstractDataExporter<TextFeature>{

	
	public TextFeatureExporter() {
		super();
	}
	
	@Override
	public void exportDataToCsv(List<TextFeature> data, String[] headerRecord, String fileName, CsvOptions options) {
		
	}

	public void exportDataToDatabase() {
		TextRepository textRepo = new TextRepository();
		List<Text> texts = textRepo.findAllTexts().stream().filter(text -> !StringUtils.isBlank(text.getPrepMessage())).collect(Collectors.toList());
		texts.stream().forEach(text -> {
			if (!StringUtils.isBlank(text.getPrepMessage())) {
				Map<String, Double> f = getVectorFeatures(text);
				FeatureRepository featureRepo = new FeatureRepository();
				factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
				EntityManager em = factory.createEntityManager();
				for (Entry<String, Double> entry : f.entrySet()) {
					if(!entry.getValue().equals(0.0)) {
						Feature feature = featureRepo.findFeatureByDescription(entry.getKey()).get(0);
						TextFeature textFeature = new TextFeature();
						textFeature.setFeature(feature);
						textFeature.setText(text);
						textFeature.setValue(entry.getValue());
						em.getTransaction().begin();
						em.persist(textFeature);
						em.getTransaction().commit();
					}
				}
				em.close();
				factory.close();
			}
		});
	}

}
