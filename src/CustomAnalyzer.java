import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CustomAnalyzer extends Analyzer {

	public static final int maxTokenLength = 255;

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
      final StandardTokenizer src = new StandardTokenizer();
      src.setMaxTokenLength(maxTokenLength);
      TokenStream tok = new StandardFilter(src);
      tok = new LowerCaseFilter(tok);

			// 0 = "DEFAULT STOP DEFAULT STEM", 1 = "NO STOP NO STEM", 2 = "DEFAULT STOP NO STEM", 3 = "INDRI STOP K STEM"
			int linguisticProcessing = 0;

			if(linguisticProcessing == 0) {
	      // DEFAULT STOP DEFAULT STEM
	      tok = new StopFilter(tok, EnglishAnalyzer.getDefaultStopSet());
	      tok = new PorterStemFilter(tok);
			} else if (linguisticProcessing == 1) {
      	// NO STOP NO STEM
				// do nothing
			} else if (linguisticProcessing == 2) {
	      // DEFAULT STOP NO STEM
	      tok = new StopFilter(tok, EnglishAnalyzer.getDefaultStopSet());
			} else if (linguisticProcessing == 4) {
	      // INDRI STOP K STEM
				Reader reader;
				StopAnalyzer analyzer;
				try {
					reader = new FileReader("inquery-stopwords.txt");
					analyzer = new StopAnalyzer(reader);
					tok = new StopFilter(tok, analyzer.getStopwordSet());
				} catch (Exception e) {
					System.out.println(e);
				}
				tok = new KStemFilter(tok);
			}

      return new TokenStreamComponents(src, tok) {
        @Override
        protected void setReader(final Reader reader) {
          src.setMaxTokenLength(maxTokenLength);
          super.setReader(reader);
        }
      };
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
      TokenStream result = new StandardFilter(in);
      result = new LowerCaseFilter(result);
      return result;
    }
}
