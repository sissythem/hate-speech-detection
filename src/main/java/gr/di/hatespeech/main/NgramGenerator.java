package gr.di.hatespeech.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import gr.di.hatespeech.dataexporters.FeatureExporter;
import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.features.*;
import gr.di.hatespeech.repositories.FeatureRepository;
import gr.di.hatespeech.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Logger;
import gr.di.hatespeech.utils.LoggerFactory;
import weka.core.tokenizers.CharacterNGramTokenizer;
import weka.core.tokenizers.NGramTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class NgramGenerator {
	private static final Logger logger = LoggerFactory.getLogger(NgramGenerator.class);
	protected EntityManagerFactory factory;
	private Map<String, Double> allTokens = new HashMap<>();
	private static final String NGRAM_KEY_PREFIX = "ngramfeatures/";
	private static final String CHAR_NGRAM_KEY_PREFIX = "charngramfeatures/";
	
	public static void main(String[] args) {
		NgramGenerator ngramGenerator = new NgramGenerator();
//		ngramGenerator.mergeInstances();
//		ngramGenerator.getAllNgrams();
//		ngramGenerator.produceAllCharNGrams();
//		ngramGenerator.generateFeatures();
//		ngramGenerator.read("./ngrams.ser");
//		ngramGenerator.read("./charngrams.ser");
		ngramGenerator.extractNgrams();
	}
	
	private void mergeInstances() {
		InstanceGenerator instanceGenerator = new InstanceGenerator();
		instanceGenerator.mergeAllGeneratedinstances("./instances/singlelabel/", 10, "train");
		instanceGenerator.mergeAllGeneratedinstances("./instances/singlelabel/", 10, "test");
	}

	private void extractNgrams() {
		NgramFeatureExtractor ngramFeatureExtractor = new NgramFeatureExtractor(NGRAM_KEY_PREFIX);
		CharacterNGramFeatureExtractor characterNGramFeatureExtractor = new CharacterNGramFeatureExtractor(CHAR_NGRAM_KEY_PREFIX);
		extractTextFeatures(ngramFeatureExtractor, characterNGramFeatureExtractor);
	}

	private void extractTextFeatures(NgramFeatureExtractor ngramFeatureExtractor, CharacterNGramFeatureExtractor characterNGramFeatureExtractor) {
		TextRepository textRepo = new TextRepository();
		FeatureRepository featureRepository = new FeatureRepository();
		List<Text> texts = textRepo.findAllTexts();
		
		factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		allTokens = ngramFeatureExtractor.getAllTokens();
		allTokens.putAll(characterNGramFeatureExtractor.getAllTokens());
		texts.stream()
				.filter(text -> !StringUtils.isBlank(text.getPrepMessage()) && text.getId() > 500 && text.getId()<701).forEach(text -> {
					Utils.FILE_LOGGER.info("Text: " + text.getPrepMessage());
					Map<String, Double> features = ngramFeatureExtractor.extractFeatures(text);
					features.putAll(characterNGramFeatureExtractor.extractFeatures(text));
					for (Entry<String, Double> entry : allTokens.entrySet()) {
						TextFeature textFeature = new TextFeature();
						if (features.containsKey(entry.getKey()) && !features.get(entry.getKey()).equals(0.0)) {
							Feature feature = featureRepository.findFeatureByDescription(entry.getKey()).stream()
									.findFirst().get();
							textFeature.setFeature(feature);
					textFeature.setText(text);
					textFeature.setValue(features.get(entry.getKey()));
					em.getTransaction().begin();
					em.persist(textFeature);
					em.getTransaction().commit();
				}
			}
		});
		em.close();
		factory.close();
	}

	private void generateFeatures() {
		FeatureExporter featureExporter = new FeatureExporter();
		NgramFeatureExtractor ngramFeatureExtractor = new NgramFeatureExtractor(NGRAM_KEY_PREFIX);
		CharacterNGramFeatureExtractor characterNGramFeatureExtractor = new CharacterNGramFeatureExtractor(CHAR_NGRAM_KEY_PREFIX);
		allTokens = ngramFeatureExtractor.getAllTokens();
		factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		for(Entry<String,Double> entry : allTokens.entrySet()) {
			addFeatureInDatabase(featureExporter, em, entry);
		}
		allTokens = characterNGramFeatureExtractor.getAllTokens();
		for(Entry<String,Double> entry : allTokens.entrySet()) {
			addFeatureInDatabase(featureExporter, em, entry);
		}
		em.close();
		factory.close();
	}

	public void addFeatureInDatabase(FeatureExporter featureExporter, EntityManager em, Entry<String, Double> entry) {
		Feature feature = new Feature();
		feature.setDescription(entry.getKey());
		featureExporter.addKind(feature);
		em.getTransaction().begin();
		em.persist(feature);
		em.getTransaction().commit();
	}
	
	@SuppressWarnings("unchecked")
	private void read(String filename) {
		FeatureExporter featureExporter = new FeatureExporter();
		try {
			factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
			EntityManager em = factory.createEntityManager();
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = classloader.getResourceAsStream(filename);
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			allTokens.putAll((HashMap<String, Double>) ois.readObject());
			ois.close();
			inputStream.close();
			for(String descr : allTokens.keySet()) {
				featureExporter.exportFeatureToDatabase(em, descr);
			}
			em.close();
			factory.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void getAllNgrams() {
		allTokens = new HashMap<>();
		TextRepository textRepo = new TextRepository();
		List<Text> texts = textRepo.findAllTexts();
		texts.forEach(text -> {
			List<String> tokens = getNgramTokens(text);
			tokens.forEach(token -> {
				if (!allTokens.containsKey(NGRAM_KEY_PREFIX + token)) {
					allTokens.put(NGRAM_KEY_PREFIX + token, 0.0);
				}
				allTokens.put(NGRAM_KEY_PREFIX + token, allTokens.get(NGRAM_KEY_PREFIX + token) + 1);
			});
		});
		List<Entry<String, Double>> entries = new ArrayList<>(allTokens.entrySet());
		Comparator<Entry<String, Double>> comparator = (Entry<String, Double> o1, Entry<String, Double> o2) -> - Double.compare(o1.getValue(), o2.getValue());
		entries.sort(comparator);
		entries = entries.stream().limit(100).collect(Collectors.toList());
		allTokens = new HashMap<>();
		entries.forEach(entry -> allTokens.put(entry.getKey(), 0.0));
		try {
			FileOutputStream fos = new FileOutputStream("./ngrams.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(allTokens);
			oos.close();
			fos.close();
			logger.info("Serialized HashMap data is saved in ngrams.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void produceAllCharNGrams() {
		allTokens = new HashMap<>();
		TextRepository textRepo = new TextRepository();
		List<Text> texts = textRepo.findAllTexts();
		texts.forEach(text -> {
			List<String> tokens = getCharacterNgramTokens(text);
			tokens.forEach(token -> {
				if (!allTokens.containsKey(CHAR_NGRAM_KEY_PREFIX + token)) {
					allTokens.put(CHAR_NGRAM_KEY_PREFIX + token, 0.0);
				}
				allTokens.put(CHAR_NGRAM_KEY_PREFIX + token, allTokens.get(CHAR_NGRAM_KEY_PREFIX + token) + 1);
			});
		});
		List<Entry<String, Double>> entries = new ArrayList<>(allTokens.entrySet());
		Comparator<Entry<String, Double>> comparator = (Entry<String, Double> o1, Entry<String, Double> o2) -> - Double.compare(o1.getValue(), o2.getValue());
		entries.sort(comparator);
		entries = entries.stream().limit(100).collect(Collectors.toList());
		allTokens = new HashMap<>();
		entries.forEach(entry -> allTokens.put(entry.getKey(), 0.0));
		try {
			FileOutputStream fos = new FileOutputStream("./charngrams.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(allTokens);
			oos.close();
			fos.close();
			logger.info("Serialized HashMap data is saved in charngrams.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private List<String> getNgramTokens(Text text) {
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(1);
		tokenizer.setNGramMaxSize(3);
		tokenizer.tokenize(text.getPrepMessage());
		List<String> elements = new ArrayList<>();
		String token = tokenizer.nextElement();
		while (!StringUtils.isBlank(token)) {
			elements.add(token);
			token = tokenizer.nextElement();
		}
		return elements;
	}

	private List<String> getCharacterNgramTokens(Text text) {
		CharacterNGramTokenizer tokenizer = new CharacterNGramTokenizer();
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(3);
		tokenizer.tokenize(text.getPrepMessage());
		List<String> elements = new ArrayList<>();
		String token = tokenizer.nextElement();
		while (!StringUtils.isBlank(token)) {
			elements.add(token);
			token = tokenizer.nextElement();
		}
		return elements;
	}
}
