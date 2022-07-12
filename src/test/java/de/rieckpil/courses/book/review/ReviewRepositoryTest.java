package de.rieckpil.courses.book.review;

import de.rieckpil.courses.book.management.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
  "spring.flyway.enabled=false",
  "spring.jpa.hibernate.ddl-auto=create-drop",
  "spring.datasource.driver-class-name=com.p6spy.engine.spy.P6SpyDriver",
  "spring.datasource.url=jdbc:p6spy:h2:mem:testing;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {
  @Autowired
  private EntityManager entityManager;

  @Autowired
  private ReviewRepository cut;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private TestEntityManager testEntityManager;

  @BeforeEach
  void beforeEach() {
    assertEquals(0, cut.count());
  }

  @Test
  void notNull() throws SQLException {
    assertNotNull(entityManager);
    assertNotNull(cut);
    assertNotNull(dataSource);
    assertNotNull(testEntityManager);


    System.out.println(dataSource.getConnection().getMetaData().getDatabaseProductName());

    Review review = new Review();
    review.setBook(null);
    review.setUser(null);
    review.setTitle("Effective Java");
    review.setContent("Programming content");
    review.setRating(5);
    review.setCreatedAt(LocalDateTime.now());

    // Review result = cut.save(review);
    Review result = testEntityManager.persistFlushFind(review);
  }

  @Test
  void transactionSupport() {
    Review review = new Review();
    review.setBook(null);
    review.setUser(null);
    review.setTitle("Effective Java");
    review.setContent("Programming content");
    review.setRating(5);
    review.setCreatedAt(LocalDateTime.now());

    Review result = cut.save(review);
  }
}
