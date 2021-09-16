package de.rieckpil.courses.book.review;

import de.rieckpil.courses.book.review.RandomReviewParameterResolverExtension.RandomReview;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RandomReviewParameterResolverExtension.class)
public class ReviewVerifierTest {
  private ReviewVerifier reviewVerifier;

  @BeforeEach
  public void setup() {
    reviewVerifier = new ReviewVerifier();
  }

  @Test
  @DisplayName("Should fail when review contains swear words")
  public void shouldFailWhenReviewContainsSwearWords() {
    String review = "This book is shit";
    ReviewVerifier reviewVerifier = new ReviewVerifier();
    System.out.println("Testing a review");
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect swear word");
  }

  @Test
  @DisplayName("Should fail if review has lorem ipsum")
  public void shouldFailIfReviewHasLoremIpsum() {
    String review = """
      Lorem ipsum is simply dummy text of the printing and typesetting industry.
      Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a
      galley of type and scrambled it to make a type specimen book.
      """;

    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect lorem ipsum");
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/badReview.csv")
  public void shouldFailWhenReviewIsOfBadQuality(String review) throws InterruptedException {
    Thread.sleep(2000);
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect  bad review");
  }

  @RepeatedTest(5)
  public void shouldFailWhenRandomReviewQualityIsBad(@RandomReview String review) throws InterruptedException {
    Thread.sleep(1000);
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect  bad review");
  }

  @Test
  void shouldPassWhenReviewIsBad() {
    String review = "I can totally recommend this book anyone who is interested in learning how to write Java code";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertTrue(result, "ReviewVerifier did not pass a good review");
  }

  @Test
  public void shouldOassWhenReviewIsGood() {
    String review  = "I can totally recommend this book who is interested in learning how to write Java code!";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertTrue(result, "Review did not pass a good review");
  }

  @Test
  public void shouldOassWhenReviewIsHamcrest() {
    String review  = "I can totally recommend this book who is interested in learning how to write Java code!";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    // assertTrue(result, "Review did not pass a good review");
    MatcherAssert.assertThat("Review did not pass a good review", result, Matchers.equalTo(true));
  }

  @Test
  public void shouldOassWhenReviewIsGoodAssertJ() {
    String review  = "I can totally recommend this book who is interested in learning how to write Java code!";
    boolean result = reviewVerifier.doesMeetQualityStandards(review);

  }
}
