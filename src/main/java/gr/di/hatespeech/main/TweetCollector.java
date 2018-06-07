package gr.di.hatespeech.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.parsers.TweetParser;
import gr.di.hatespeech.preprocessors.TextPreprocessor;
import gr.di.hatespeech.readers.SourceCsvReader;
import gr.di.hatespeech.readers.TxtReader;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Logger;
import gr.di.hatespeech.utils.LoggerFactory;
import gr.di.hatespeech.utils.Utils;

public class TweetCollector {
	private static final Logger logger = LoggerFactory.getLogger(TweetCollector.class);
	protected static EntityManagerFactory factory;
	protected TxtReader reader;
	protected Map<String, List<Double>> pretrainedWord2Vec;
	private SourceCsvReader sourceCsvReader = new SourceCsvReader();
	private TweetParser tweetParser = new TweetParser();
	private static TextPreprocessor textPreprocessor = new TextPreprocessor(false, Utils.STOPWORDS_CSV_PATH);

	public static void main(String[] args) {
		TweetCollector tweetCollector = new TweetCollector();
		tweetCollector.exportTexts();
		tweetCollector.readPretrainedVectors("./word2vec/glove.6B.50d.txt");
		tweetCollector.serializeWord2Vec();
		tweetCollector.preprocessAndSaveTexts();
	}

	private void exportTexts() {
		tweetParser.parseData(Utils.TWEETS_TO_DOWNLOAD_FILE_PATH);
		sourceCsvReader.readData(Utils.EXISTING_TWEETS_FILE_PATH);
	}

	@SuppressWarnings("unchecked")
	private void serializeWord2Vec() {
		this.pretrainedWord2Vec = new HashMap<>();
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try {
			InputStream inputStream = classloader.getResourceAsStream(Utils.WORD2VEC_TWITTER_PART_1_SER);
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			pretrainedWord2Vec.putAll((HashMap<String, List<Double>>) ois.readObject());
			ois.close();
			inputStream.close();

			InputStream inputStream2 = classloader.getResourceAsStream(Utils.WORD2VEC_TWITTER_PART_2_SER);
			ObjectInputStream ois2 = new ObjectInputStream(inputStream2);
			pretrainedWord2Vec.putAll((HashMap<String, List<Double>>) ois2.readObject());
			ois.close();
			inputStream.close();
		} catch (IOException ioe) {
			logger.error("File not found!");
			logger.error(ioe.getMessage(),ioe);
			return;
		} catch (ClassNotFoundException c) {
			logger.error("Class not found");
			logger.error(c.getMessage(),c);
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream("./glove.6B.50d.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(pretrainedWord2Vec);
			oos.close();
			fos.close();
			logger.info("Serialized HashMap data is saved in word2vec.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	protected void readPretrainedVectors(String fileName) {
		this.reader = new TxtReader();
		this.pretrainedWord2Vec = new HashMap<>();
		List<String> txtLines = reader.readData(fileName);
		txtLines.stream().forEach(line -> {
			logger.info("Line #" + txtLines.indexOf(line));
			List<String> lineData = new LinkedList<String>(Arrays.asList(line.split(" ")));
			String word = lineData.remove(0);
			logger.info("Adding word: " + word);
			List<Double> vector = lineData.stream().map(Double::valueOf).collect(Collectors.toList());
			pretrainedWord2Vec.put(word, vector);
		});
	}

	protected void preprocessAndSaveTexts() {
		TextRepository textRepo = new TextRepository();
		List<Text> texts = textRepo.findAllTexts();
		texts = textPreprocessor.preprocessTexts(texts);
		factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		texts.stream().forEach(text -> {
//			if (StringUtils.isBlank(text.getPrepMessage())) {
//				text.setPrepMessage(text.getBody());
//			}
			em.getTransaction().begin();
			em.merge(text);
			em.getTransaction().commit();
		});
		em.close();
		factory.close();
	}

}
