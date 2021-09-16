package de.rieckpil.courses.book;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class JsonTest {

  @Test
  public void testWithJsonAssert() throws JSONException {
    String result = """
      {\"name\": \"duke\", \"age\":12, \"hobbies\":[\"Soccer\", \"Java\"]}
      """;
    JSONAssert.assertEquals("{\"name\": \"duke\"}", result, false);
  }
}
