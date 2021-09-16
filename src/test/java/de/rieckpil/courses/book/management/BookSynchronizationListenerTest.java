package de.rieckpil.courses.book.management;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookSynchronizationListenerTest {
  private final String ISBN = "1234567891234";
  @Mock
  private BookRepository mockBookRepository;
  @Mock
  private OpenLibraryApiClient mockOpenLibraryApiClient;
  @InjectMocks
  private BookSynchronizationListener cut;
  @Captor
  private ArgumentCaptor<Book> bookArgumentCaptor;

  @Test
  @DisplayName("Should reject book when Isbn is malformed")
  public void shouldRejectBookWhenIsbnIsMalformed() {
    BookSynchronization bookSynchronization = new BookSynchronization("42");
    cut.consumeBookUpdates(bookSynchronization);
    verifyNoInteractions(mockOpenLibraryApiClient, mockBookRepository);
  }

  @Test
  @DisplayName("Should not override when book already exists")
  public void shouldNotOverrideWhenAlreadyExists() {
    BookSynchronization bookSynchronization = new BookSynchronization(ISBN);
    when(mockBookRepository.findByIsbn(ISBN)).thenReturn(new Book());
    cut.consumeBookUpdates(bookSynchronization);
    verifyNoInteractions(mockOpenLibraryApiClient);
    verify(mockBookRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Should throw exception when processing failed")
  public void shouldThrowExceptionWhenProcessingFailed() {
    BookSynchronization bookSynchronization = new BookSynchronization(ISBN);
    when(mockBookRepository.findByIsbn(ISBN)).thenReturn(null);
    when(mockOpenLibraryApiClient.fetchMetadataForBook(ISBN)).thenThrow(new RuntimeException("Network timeout"));
    assertThrows(RuntimeException.class, () -> cut.consumeBookUpdates(bookSynchronization));
  }

  @Test
  @DisplayName("Should store book when new and correct isbn")
  public void shouldStoreBookWhenNewAndCorrectIsbn() {
    BookSynchronization bookSynchronization = new BookSynchronization(ISBN);
    when(mockBookRepository.findByIsbn(ISBN)).thenReturn(null);
    Book rb = new Book();
    rb.setTitle("Java book");
    rb.setIsbn(ISBN);

    when(mockOpenLibraryApiClient.fetchMetadataForBook(ISBN)).thenReturn(rb);
    when(mockBookRepository.save(any())).then(invocationOnMock -> {
      Book methodArgument = invocationOnMock.getArgument(0);
      methodArgument.setId(1l);
      return methodArgument;
    });

    cut.consumeBookUpdates(bookSynchronization);
    verify(mockBookRepository).save(bookArgumentCaptor.capture());

    Book methodArgument = bookArgumentCaptor.getValue();
    assertThat(methodArgument.getTitle()).isEqualTo("Java book");
    assertThat(methodArgument.getIsbn()).isEqualTo(ISBN);
  }
}
