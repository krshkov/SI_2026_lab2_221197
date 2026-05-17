import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class SI2026Lab2Test {

    @Test
    public void searchBookEveryStatementTest() {
        IllegalArgumentException invalidTitle = assertThrows(
                IllegalArgumentException.class,
                () -> new Library().searchBookByTitle("")
        );
        assertEquals("Invalid title", invalidTitle.getMessage());

        Book borrowedDune = new Book("Dune", "Frank Herbert", "Science Fiction");
        borrowedDune.setBorrowed(true);
        Book availableDune = new Book("Dune", "Frank Herbert", "Science Fiction");
        Library foundLibrary = libraryWith(
                borrowedDune,
                availableDune,
                new Book("Foundation", "Isaac Asimov", "Science Fiction")
        );

        List<Book> foundBooks = foundLibrary.searchBookByTitle("Dune");
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertSame(availableDune, foundBooks.get(0));

        Book onlyBorrowedDune = new Book("Dune", "Frank Herbert", "Science Fiction");
        onlyBorrowedDune.setBorrowed(true);
        Library emptyResultLibrary = libraryWith(
                onlyBorrowedDune,
                new Book("Hyperion", "Dan Simmons", "Science Fiction")
        );
        assertNull(emptyResultLibrary.searchBookByTitle("Dune"));
    }

    @Test
    public void borrowBookEveryBranchTest() {
        IllegalArgumentException invalidTitle = assertThrows(
                IllegalArgumentException.class,
                () -> new Library().borrowBook("", "Frank Herbert")
        );
        assertEquals("Invalid search query", invalidTitle.getMessage());

        IllegalArgumentException invalidAuthor = assertThrows(
                IllegalArgumentException.class,
                () -> new Library().borrowBook("Dune", "")
        );
        assertEquals("Invalid search query", invalidAuthor.getMessage());

        Book otherBook = new Book("Foundation", "Isaac Asimov", "Science Fiction");
        Book availableDune = new Book("Dune", "Frank Herbert", "Science Fiction");
        Library successLibrary = libraryWith(otherBook, availableDune);

        successLibrary.borrowBook("Dune", "Frank Herbert");
        assertFalse(otherBook.isBorrowed());
        assertTrue(availableDune.isBorrowed());

        Book borrowedDune = new Book("Dune", "Frank Herbert", "Science Fiction");
        borrowedDune.setBorrowed(true);
        Library alreadyBorrowedLibrary = libraryWith(borrowedDune);

        RuntimeException alreadyBorrowed = assertThrows(
                RuntimeException.class,
                () -> alreadyBorrowedLibrary.borrowBook("Dune", "Frank Herbert")
        );
        assertEquals("Book is already borrowed.", alreadyBorrowed.getMessage());

        Library notFoundLibrary = libraryWith(
                new Book("Dune", "Brian Herbert", "Science Fiction"),
                new Book("Hyperion", "Dan Simmons", "Science Fiction")
        );
        RuntimeException notFound = assertThrows(
                RuntimeException.class,
                () -> notFoundLibrary.borrowBook("Dune", "Frank Herbert")
        );
        assertEquals("Book not found", notFound.getMessage());
    }

    @Test
    public void searchBookMultipleConditionTest() {
        Book titleMatchesAndAvailable = new Book("Dune", "Frank Herbert", "Science Fiction");
        Book titleMatchesButBorrowed = new Book("Dune", "Brian Herbert", "Science Fiction");
        titleMatchesButBorrowed.setBorrowed(true);
        Book differentTitleAndAvailable = new Book("Foundation", "Isaac Asimov", "Science Fiction");
        Book differentTitleAndBorrowed = new Book("Neuromancer", "William Gibson", "Science Fiction");
        differentTitleAndBorrowed.setBorrowed(true);

        Library library = libraryWith(
                titleMatchesAndAvailable,
                titleMatchesButBorrowed,
                differentTitleAndAvailable,
                differentTitleAndBorrowed
        );

        List<Book> foundBooks = library.searchBookByTitle("Dune");
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertSame(titleMatchesAndAvailable, foundBooks.get(0));
    }

    @Test
    public void borrowBookMultipleConditionTest() {
        IllegalArgumentException bothEmpty = assertThrows(
                IllegalArgumentException.class,
                () -> new Library().borrowBook("", "")
        );
        assertEquals("Invalid search query", bothEmpty.getMessage());

        IllegalArgumentException titleEmpty = assertThrows(
                IllegalArgumentException.class,
                () -> new Library().borrowBook("", "Frank Herbert")
        );
        assertEquals("Invalid search query", titleEmpty.getMessage());

        IllegalArgumentException authorEmpty = assertThrows(
                IllegalArgumentException.class,
                () -> new Library().borrowBook("Dune", "")
        );
        assertEquals("Invalid search query", authorEmpty.getMessage());

        Book availableDune = new Book("Dune", "Frank Herbert", "Science Fiction");
        Library library = libraryWith(availableDune);

        library.borrowBook("Dune", "Frank Herbert");
        assertTrue(availableDune.isBorrowed());
    }

    private Library libraryWith(Book... books) {
        Library library = new Library();
        for (Book book : books) {
            library.addBook(book);
        }
        return library;
    }
}
