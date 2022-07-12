package de.rieckpil.courses.book.review;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RandomReviewParameterResolverExtension.class)
class ReviewVerifierTest {

  private ReviewVerifier reviewVerifier;

  @BeforeEach
  void setup() {
    reviewVerifier = new ReviewVerifier();
  }

  @AfterEach
  void tearDown() {
    System.out.println("After each");
  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("Before all");
  }

  @BeforeAll
  static void afterAll() {
    System.out.println("After All");
  }

  @Test
  void shouldFailWhenReviewContainsSwearWords() {
    String review = "This book is shit";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "Review verifier did not detect swear word");
  }


  @Test
  @DisplayName("should Fail When Review Contain Lorem")
  void testLorem() {
    String review = "Lorem ipsum dolor sit amet, connrenn jebag verjgn tejjni trbjtee" +
      "kknebkn" +
      "jnbjean" +
      "jabgjurb ihni ijio ";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "Review contains Lorem Ipsum");
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/badReview.csv")
  void shouldFailWhenReviewIsOfBadQuality(String review) throws InterruptedException {
    Thread.sleep(1000);
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect bad review");
  }

  //@Test
  @RepeatedTest(5)
  void shouldFailWhenRandomReviewQualityIsBad(@RandomReviewParameterResolverExtension.RandomReview String review) {
    System.out.println(review);
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect random bad review");
  }

  @Test
  void shouldPassWhenReviewIsGood() {
    String review = "This is a great book for anyone who wants to learn Java in depth. I can recommend it";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertTrue(result, "ReviewVerifier did not detect a bad review");
  }

  @Test
  void shouldPassWhenReviewIsGoodHamCrest() {
    String review = "This is a great book for anyone who wants to learn Java in depth. I can recommend it";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    // assertTrue(result, "ReviewVerifier did not detect a bad review");
    MatcherAssert.assertThat("Did not pass as a good review", result, Matchers.equalTo(true));
  }

  @Test
  void shouldPassWhenReviewIsGoodAssertJ() {
    String review = "This is a great book for anyone who wants to learn Java in depth. I can recommend it";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    // assertTrue(result, "ReviewVerifier did not detect a bad review");
    org.assertj.core.api.Assertions.assertThat(result).withFailMessage("Reviewverifier failed").isEqualTo(true);
  }

  @Test
  void testWithJSONAssert() throws JSONException {
    String json = "  {\n" +
      "    \"installmentAmount\": 3279.25,\n" +
      "    \"totalOutstandingAmount\": 46991.59,\n" +
      "    \"principalPaymentAmount\": 3008.42,\n" +
      "    \"interestPaymentAmount\": 270.84,\n" +
      "    \"totalAdjustedAmount\": null,\n" +
      "    \"dueDate\": \"2022-02-01\",\n" +
      "    \"overdueDate\": \"2022-02-02\",\n" +
      "    \"scheduleNumber\": 1,\n" +
      "    \"overdue\": false\n" +
      "  }";

    JSONAssert.assertEquals("{\"installmentAmount\":3279.25}", json, false);
  }

  @Test
  void testWithJsonPath() {
    String json = "{\"installmentAmount\":3279.25," +
      "\"totalOutstandingAmount\":46991.59," +
      "\"principalPaymentAmount\":3008.42," +
      "\"interestPaymentAmount\":270.84," +
      "\"totalAdjustedAmount\":null," +
      "\"dueDate\":\"2022-02-01\"," +
      "\"overdueDate\":\"2022-02-02\"," +
      "\"scheduleNumber\":1," +
      "\"overdue\":false," +
      "\"test\":[12,32,45,90]}";
    Assertions.assertEquals(4, JsonPath.parse(json).read("$.test.length()", Long.class));
    Assertions.assertEquals(3279.25, JsonPath.parse(json).read("$.installmentAmount", Double.class));
    Assertions.assertEquals(179.0, JsonPath.parse(json).read("$.test.sum()", Double.class));
  }
}
