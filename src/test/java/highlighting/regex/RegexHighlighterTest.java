package highlighting.regex;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegexHighlighterTest {

  private RegexHighlighter highlighter;

  @BeforeEach
  void setUp() {
    highlighter = new RegexHighlighter();
  }


  @Test
  void leerstring_gibLeereListe() {
    assertTrue(highlighter.computeRegions("").isEmpty());
  }

  @Test
  void textOhneMatches_gibLeereListe() {
    assertTrue(highlighter.computeRegions("x = 1 + 2;").isEmpty());
  }

  @Test
  void einzelnesKeyword_eineRegion() {
    List<HighlightRegion> r = highlighter.computeRegions("return x;");
    assertEquals(1, r.size());
    assertEquals(MiniJavaColours.KEYWORD_COLOUR, r.get(0).colour());
  }


  @Test
  void aufeinanderfolgendeRegionen_beideBehalten() {
    List<HighlightRegion> r = highlighter.computeRegions("return null;");
    assertEquals(2, r.size());
  }


  @Test
  void keywordInZeilenkommentar_kommentarGewinnt() {
    List<HighlightRegion> r = highlighter.computeRegions("// return null");
    assertEquals(1, r.size());
    assertEquals(MiniJavaColours.LINE_COMMENT_COLOUR, r.get(0).colour());
  }

  @Test
  void keywordInBlockkommentar_kommentarGewinnt() {
    List<HighlightRegion> r = highlighter.computeRegions("/* return */");
    assertEquals(1, r.size());
    assertEquals(MiniJavaColours.BLOCK_COMMENT_COLOUR, r.get(0).colour());
  }

  @Test
  void javadocVsBlockkommentar_javadocGewinnt() {
    List<HighlightRegion> r = highlighter.computeRegions("/** doc */");
    assertEquals(1, r.size());
    assertEquals(MiniJavaColours.JAVADOC_COMMENT_COLOUR, r.get(0).colour());
  }

  @Test
  void stringMitKeywordDarin_stringGewinnt() {
    List<HighlightRegion> r = highlighter.computeRegions("\"return\"");
    assertEquals(1, r.size());
    assertEquals(MiniJavaColours.STRING_LITERAL_COLOUR, r.get(0).colour());
  }


  @Test
  void keineUeberlappungenInAusgabe() {
    String code = "public class Foo() { // TODO\n  return \"bar\";\n}";
    List<HighlightRegion> r = highlighter.computeRegions(code);
    for (int i = 0; i < r.size() - 1; i++) {
      assertTrue(r.get(i).end() <= r.get(i + 1).start(), "Überlappung bei Index " + i);
    }
  }
}
