package highlighting.presets;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import highlighting.regex.Token;
import java.awt.Color;
import java.util.List;
import org.junit.jupiter.api.Test;

class MiniJavaTokensTest {

  private Token tokenByColour(Color colour) {
    return MiniJavaTokens.defaultTokens().stream()
        .filter(t -> t.colour().equals(colour))
        .findFirst()
        .orElseThrow();
  }

  // ----------------------------------------------------------------
  // String-Literale
  // ----------------------------------------------------------------

  @Test
  void string_amAnfang() {
    Token t = tokenByColour(MiniJavaColours.STRING_LITERAL_COLOUR);
    List<HighlightRegion> r = t.test("\"hallo\" rest");
    assertEquals(1, r.size());
    assertEquals(0, r.get(0).start());
  }

  @Test
  void string_inDerMitte() {
    Token t = tokenByColour(MiniJavaColours.STRING_LITERAL_COLOUR);
    List<HighlightRegion> r = t.test("x = \"hallo\";");
    assertEquals(1, r.size());
    assertEquals(4, r.get(0).start());
    assertEquals(11, r.get(0).end());
  }

  @Test
  void string_amEnde() {
    Token t = tokenByColour(MiniJavaColours.STRING_LITERAL_COLOUR);
    List<HighlightRegion> r = t.test("return \"end\"");
    assertEquals(1, r.size());
    assertEquals(12, r.get(0).end()); // "return \"end\"" hat Länge 12
  }

  @Test
  void string_mehrereImText() {
    Token t = tokenByColour(MiniJavaColours.STRING_LITERAL_COLOUR);
    assertEquals(2, t.test("\"a\" + \"b\"").size());
  }

  @Test
  void string_keinTreffer() {
    Token t = tokenByColour(MiniJavaColours.STRING_LITERAL_COLOUR);
    assertTrue(t.test("int x = 1;").isEmpty());
  }

  @Test
  void string_enthaeltSlashSlash() {
    Token t = tokenByColour(MiniJavaColours.STRING_LITERAL_COLOUR);
    assertEquals(1, t.test("\"url // pfad\"").size());
  }

  @Test
  void string_enthaeltBlockkommentar() {
    Token t = tokenByColour(MiniJavaColours.STRING_LITERAL_COLOUR);
    assertEquals(1, t.test("\"foo /* bar */\"").size());
  }

  // ----------------------------------------------------------------
  // Char-Literale
  // ----------------------------------------------------------------

  @Test
  void char_amAnfang() {
    Token t = tokenByColour(MiniJavaColours.CHAR_LITERAL_COLOUR);
    List<HighlightRegion> r = t.test("'a' + x");
    assertEquals(1, r.size());
    assertEquals(0, r.get(0).start());
  }

  @Test
  void char_inDerMitte() {
    Token t = tokenByColour(MiniJavaColours.CHAR_LITERAL_COLOUR);
    assertEquals(1, t.test("char c = 'a';").size());
  }

  @Test
  void char_escape() {
    Token t = tokenByColour(MiniJavaColours.CHAR_LITERAL_COLOUR);
    assertEquals(1, t.test("char nl = '\\n';").size());
  }

  @Test
  void char_mehrereImText() {
    Token t = tokenByColour(MiniJavaColours.CHAR_LITERAL_COLOUR);
    assertEquals(2, t.test("'a' == 'b'").size());
  }

  @Test
  void char_keinTreffer() {
    Token t = tokenByColour(MiniJavaColours.CHAR_LITERAL_COLOUR);
    assertTrue(t.test("int x = 1;").isEmpty());
  }

  // ----------------------------------------------------------------
  // Keywords
  // ----------------------------------------------------------------

  @Test
  void keyword_amAnfang() {
    Token t = tokenByColour(MiniJavaColours.KEYWORD_COLOUR);
    List<HighlightRegion> r = t.test("class Foo {}");
    assertEquals(1, r.size());
    assertEquals(0, r.get(0).start());
  }

  @Test
  void keyword_inDerMitte() {
    Token t = tokenByColour(MiniJavaColours.KEYWORD_COLOUR);
    assertEquals(1, t.test("x = null;").size());
  }

  @Test
  void keyword_amEnde() {
    Token t = tokenByColour(MiniJavaColours.KEYWORD_COLOUR);
    List<HighlightRegion> r = t.test("x = null");
    assertEquals(1, r.size());
    assertEquals(8, r.get(0).end());
  }

  @Test
  void keyword_mehrereImText() {
    Token t = tokenByColour(MiniJavaColours.KEYWORD_COLOUR);
    assertEquals(4, t.test("public class Foo { return null; }").size());
  }

  @Test
  void keyword_keinTreffer() {
    Token t = tokenByColour(MiniJavaColours.KEYWORD_COLOUR);
    assertTrue(t.test("foo bar baz").isEmpty());
  }

  @Test
  void keyword_nichtInnerhalbBezeichner() {
    Token t = tokenByColour(MiniJavaColours.KEYWORD_COLOUR);
    assertTrue(t.test("classes returnValue nullValue").isEmpty());
  }

  // ----------------------------------------------------------------
  // Annotationen
  // ----------------------------------------------------------------

  @Test
  void annotation_amAnfang() {
    Token t = tokenByColour(MiniJavaColours.ANNOTATION_COLOUR);
    List<HighlightRegion> r = t.test("@Override");
    assertEquals(1, r.size());
    assertEquals(0, r.get(0).start());
  }

  @Test
  void annotation_direktAmZeilenanfang() {
    Token t = tokenByColour(MiniJavaColours.ANNOTATION_COLOUR);
    assertEquals(1, t.test("\n@Override").size());
  }

  @Test
  void annotation_mitLeerzeichenDavor() {
    Token t = tokenByColour(MiniJavaColours.ANNOTATION_COLOUR);
    assertEquals(1, t.test("  @Deprecated").size());
  }

  @Test
  void annotation_mehrereImText() {
    Token t = tokenByColour(MiniJavaColours.ANNOTATION_COLOUR);
    assertEquals(2, t.test("@Override @Deprecated").size());
  }

  @Test
  void annotation_keinTreffer() {
    Token t = tokenByColour(MiniJavaColours.ANNOTATION_COLOUR);
    assertTrue(t.test("kein at zeichen hier").isEmpty());
  }

  // ----------------------------------------------------------------
  // Zeilenkommentare
  // ----------------------------------------------------------------

  @Test
  void zeilenkommentar_amAnfang() {
    Token t = tokenByColour(MiniJavaColours.LINE_COMMENT_COLOUR);
    List<HighlightRegion> r = t.test("// Kommentar");
    assertEquals(1, r.size());
    assertEquals(0, r.get(0).start());
  }

  @Test
  void zeilenkommentar_nachCode() {
    Token t = tokenByColour(MiniJavaColours.LINE_COMMENT_COLOUR);
    assertEquals(1, t.test("int x = 1; // Zuweisung").size());
  }

  @Test
  void zeilenkommentar_mehrereZeilen() {
    Token t = tokenByColour(MiniJavaColours.LINE_COMMENT_COLOUR);
    assertEquals(2, t.test("// erste\n// zweite").size());
  }

  @Test
  void zeilenkommentar_keinTreffer() {
    Token t = tokenByColour(MiniJavaColours.LINE_COMMENT_COLOUR);
    assertTrue(t.test("int x = 1;").isEmpty());
  }

  // ----------------------------------------------------------------
  // Block-Kommentare
  // ----------------------------------------------------------------

  @Test
  void blockkommentar_inDerMitte() {
    Token t = tokenByColour(MiniJavaColours.BLOCK_COMMENT_COLOUR);
    assertEquals(1, t.test("int x = /* Kommentar */ 1;").size());
  }

  @Test
  void blockkommentar_mehrzeilig() {
    Token t = tokenByColour(MiniJavaColours.BLOCK_COMMENT_COLOUR);
    assertEquals(1, t.test("/* Zeile 1\nZeile 2 */").size());
  }

  @Test
  void blockkommentar_keinTreffer() {
    Token t = tokenByColour(MiniJavaColours.BLOCK_COMMENT_COLOUR);
    assertTrue(t.test("int x = 1;").isEmpty());
  }

  // ----------------------------------------------------------------
  // Javadoc-Kommentare
  // ----------------------------------------------------------------

  @Test
  void javadoc_amAnfang() {
    Token t = tokenByColour(MiniJavaColours.JAVADOC_COMMENT_COLOUR);
    List<HighlightRegion> r = t.test("/** doc */");
    assertEquals(1, r.size());
    assertEquals(0, r.get(0).start());
  }

  @Test
  void javadoc_mehrzeilig() {
    Token t = tokenByColour(MiniJavaColours.JAVADOC_COMMENT_COLOUR);
    assertEquals(1, t.test("/**\n * @param x\n */").size());
  }

  @Test
  void javadoc_keinTreffer() {
    Token t = tokenByColour(MiniJavaColours.JAVADOC_COMMENT_COLOUR);
    assertTrue(t.test("int x = 1;").isEmpty());
  }
}
