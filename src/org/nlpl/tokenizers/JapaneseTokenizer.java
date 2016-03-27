package org.nlpl.tokenizers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nlpl.Tokenizer;

import net.java.sen.ReadingProcessor;
import net.java.sen.ReadingProcessor.ReadingResult;
import net.java.sen.SenFactory;
import net.java.sen.dictionary.Morpheme;
import net.java.sen.dictionary.Token;
import net.java.sen.filter.reading.NumberFilter;

public class JapaneseTokenizer implements Tokenizer {

	private ReadingProcessor readingProcessor;

	public JapaneseTokenizer(String configFilename) {
		configFilename = "C:/Users/Nate/Desktop/workspaces/java_workspace/java_workspace/Itadaki/GoSen/testdata/dictionary/dictionary.xml";
		readingProcessor = SenFactory.getReadingProcessor(configFilename);
		readingProcessor.addFilter(0, new NumberFilter());
	}

	@Override
	public Result tokenize(String inputText) {
		readingProcessor.setText(inputText);
		ReadingResult readingResult = readingProcessor.process();
		List<Token> senTokens = readingResult.getTokens();
		List<Result.Token> resultTokens = new LinkedList<>();
		for (Token t : senTokens) {
			Morpheme morph = t.getMorpheme();
			Map<String, String> details = new HashMap<>();
			details.put("additionalInfo", morph.getAdditionalInformation());
			details.put("POS", morph.getPartOfSpeech());
			details.put("pronunciations", String.join(", ", morph.getPronunciations()));
			details.put("readings", String.join(", ", morph.getReadings()));
			String conjForm = morph.getConjugationalForm();
			if (!conjForm.equals("*"))
				details.put("conjForm", conjForm);
			String conjType = morph.getConjugationalType();
			if (!conjType.equals("*"))
				details.put("conjType", conjType);
			resultTokens.add(new Result.Token(t.getStart(), t.end()-1, t.getMorpheme().getBasicForm(), details));
		}
		return new Result(inputText, resultTokens);
	}

	public static void main(String[] args){
		JapaneseTokenizer jt = new JapaneseTokenizer("NEED CORRECT STRING HERE");
		System.out.println(jt.tokenize("「情報スーパーハイウェイ」の真のインパクトは、情報インフラの構築により経済が従来のハードやモノづくり中心の実体経済から知識、情報、ソフトを主体とした経済に移行し、そこから生まれる新しい産業や経済活動にある。"));
	}
}
