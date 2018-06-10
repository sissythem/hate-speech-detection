package gr.di.hatespeech.test.junit.readers;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import gr.di.hatespeech.readers.TxtReader;

public class TxtReaderTest {

	private TxtReader txtReader = new TxtReader();

	@Test
	public void testTxtReader() {
		try {
			FileReader fileReader = new FileReader("./src/test/resources/test.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			List<String> data = txtReader.readLines(bufferedReader);
			assertEquals(true, data.size() == 3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
