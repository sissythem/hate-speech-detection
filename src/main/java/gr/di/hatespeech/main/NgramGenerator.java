package gr.di.hatespeech.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Logger;
import gr.di.hatespeech.utils.LoggerFactory;
import weka.core.tokenizers.CharacterNGramTokenizer;
import weka.core.tokenizers.NGramTokenizer;

public class NgramGenerator {
	private static final Logger logger = LoggerFactory.getLogger(NgramGenerator.class);
	protected Map<String, Double> allTokens = new HashMap<>();
	protected Map<String, Double> finalTokens;
	public static final String NGRAM_KEY_PREFIX = "ngramfeatures/";
	public static final String CHAR_NGRAM_KEY_PREFIX = "charngramfeatures/";
	
	public static void main(String[] args) {
		NgramGenerator ngramGenerator = new NgramGenerator();
		 ngramGenerator.getAllNgrams();
		 ngramGenerator.produceAllCharNGrams();
		 ngramGenerator.read();
	}

	@SuppressWarnings("unchecked")
	private void read() {

		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = classloader.getResourceAsStream("./ngrams.ser");
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			allTokens.putAll((HashMap<String, Double>) ois.readObject());
			ois.close();
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void getAllNgrams() {
		allTokens = new HashMap<>();
		TextRepository textRepo = new TextRepository();
		List<Text> texts = textRepo.findAllTexts();
		texts.stream().forEach(text -> {
			List<String> tokens = getNgramTokens(text);
			tokens.stream().forEach(token -> {
				if (!allTokens.containsKey(NGRAM_KEY_PREFIX + token)) {
					allTokens.put(NGRAM_KEY_PREFIX + token, 0.0);
				}
				allTokens.put(NGRAM_KEY_PREFIX + token, allTokens.get(NGRAM_KEY_PREFIX + token) + 1);
			});
		});
		List<Entry<String, Double>> entries = new ArrayList<>();
		entries.addAll(allTokens.entrySet());
		Comparator<Entry<String, Double>> comparator = new Comparator<Entry<String, Double>>() {

			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return Double.compare(o1.getValue(), o2.getValue());
			}
		};
		Collections.sort(entries, comparator);
		entries = entries.stream().limit(2000).collect(Collectors.toList());
		allTokens = new HashMap<>();
		entries.stream().forEach(entry -> {
			allTokens.put(entry.getKey(), 0.0);
		});
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
		texts.stream().forEach(text -> {
			List<String> tokens = getCharacterNgramTokens(text);
			tokens.stream().forEach(token -> {
				if (!allTokens.containsKey(CHAR_NGRAM_KEY_PREFIX + token)) {
					allTokens.put(CHAR_NGRAM_KEY_PREFIX + token, 0.0);
				}
				allTokens.put(CHAR_NGRAM_KEY_PREFIX + token, allTokens.get(CHAR_NGRAM_KEY_PREFIX + token) + 1);
			});
		});
		List<Entry<String, Double>> entries = new ArrayList<>();
		entries.addAll(allTokens.entrySet());
		Comparator<Entry<String, Double>> comparator = new Comparator<Entry<String, Double>>() {

			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return Double.compare(o1.getValue(), o2.getValue());
			}
		};
		Collections.sort(entries, comparator);
		entries = entries.stream().limit(entries.size() * 10 / 100).collect(Collectors.toList());
		allTokens = new HashMap<>();
		entries.stream().forEach(entry -> {
			allTokens.put(entry.getKey(), 0.0);
		});
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

	protected List<String> getNgramTokens(Text text) {
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
