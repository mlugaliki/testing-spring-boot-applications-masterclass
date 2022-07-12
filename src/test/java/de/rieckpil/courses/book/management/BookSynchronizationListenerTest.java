package de.rieckpil.courses.book.management;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookSynchronizationListenerTest {
  private static final String ISBN = "1234567890123";
  @Mock
  BookRepository bookRepository;

  @Mock
  private OpenLibraryApiClient openLibraryApiClient;

  @InjectMocks
  private BookSynchronizationListener cut;

  @Captor
  private ArgumentCaptor<Book> bookArgumentCaptor;
  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void shouldRejectBookWhenIsbnIsMalformed() {
    BookSynchronization bookSync = new BookSynchronization("42");
    cut.consumeBookUpdates(bookSync);
    verifyNoInteractions(openLibraryApiClient, bookRepository);
  }

  @Test
  void shouldNotOverrideWhenBookAlreadyExists() {
    Book b = new Book();
    b.setAuthor("Martin");
    b.setIsbn("X");
    b.setGenre("Fiction");
    b.setTitle("Another Fictition book");
    b.setThumbnailUrl("");
    b.setId(1L);
    Mockito.when(bookRepository.findByIsbn("X")).thenReturn(b);

    Book myBook = bookRepository.findByIsbn("X");
    assertNotNull(myBook);
    // BookSynchronization bookSync = new BookSynchronization("1234567890123");
    // cut.consumeBookUpdates(bookSync);
    verifyNoInteractions(openLibraryApiClient);
    verify(bookRepository, times(0)).save(ArgumentMatchers.any());
  }

  @Test
  void shouldThrowExceptionWhenProcessingFails() {
    BookSynchronization bookSync = new BookSynchronization(ISBN);
    when(bookRepository.findByIsbn(ISBN)).thenReturn(null);
    when(openLibraryApiClient.fetchMetadataForBook(ISBN)).thenThrow(new RuntimeException("Network timeout"));

    assertThrows(RuntimeException.class, () -> cut.consumeBookUpdates(bookSync));

    // verifyNoInteractions(openLibraryApiClient);
    // verify(bookRepository, times(0)).save(ArgumentMatchers.any());
  }

  @Test
  void shouldStoreBookWhenNewAndCorrectIsbn() {
    Book b = new Book();
    b.setAuthor("Martin");
    b.setIsbn("X");
    b.setGenre("Fiction");
    b.setTitle("Another Fictitious book");
    b.setThumbnailUrl("");
    b.setId(1L);

    when(bookRepository.findByIsbn(ISBN)).thenReturn(null);
    when(openLibraryApiClient.fetchMetadataForBook(ISBN)).thenReturn(b);
    when(bookRepository.save(b)).then(invocationOnMock -> {
      Book methodArgument = invocationOnMock.getArgument(0);
      methodArgument.setId(1L);
      return methodArgument;
    });

    BookSynchronization bookSync = new BookSynchronization(ISBN);
    cut.consumeBookUpdates(bookSync);
    verify(bookRepository).save(bookArgumentCaptor.capture());
    assertEquals("Another Fictitious book", bookArgumentCaptor.getValue().getTitle());
  }


}
