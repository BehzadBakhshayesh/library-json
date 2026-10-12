package library;

import java.io.File;
import java.util.ArrayList;

public class Library {
    private static final String BOOKS_FILE = "books.txt";
    private static final String MEMBERS_FILE = "members.txt";
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();
    private FileManager fileManager = new FileManager();

    public void addBook(String title, String author, int year) {
        Book newBook = new Book(title, author, year);
        this.books.add(newBook);
    }

    public Book findBookById(long id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    public ArrayList<Book> showBooks() {
        return new ArrayList<>(this.books);
    }

    public ArrayList<Book> searchBook(String search) {
        ArrayList<Book> searchList = new ArrayList<>();
        String searchText = search.toLowerCase();
        for (int i = 0; i < this.books.size(); i++) {
            if (this.books.get(i).getTitle().toLowerCase().contains(searchText)) {
                searchList.add(this.books.get(i));
            }
        }
        return searchList;
    }

    public Book editBook(long bookId, String title, String author, int year) {
        Book foundBook = this.findBookById(bookId);
        if (foundBook == null) {
            throw new LibraryException("Book not found! Invalid ID.");
        }
        Book.validateParams(title, author, year);
        foundBook.setTitle(title);
        foundBook.setAuthor(author);
        foundBook.setYear(year);
        return foundBook;
    }

    public void deleteBook(long bookId) {
        Book foundBook = this.findBookById(bookId);
        if (foundBook == null) {
            throw new LibraryException("An error occurred while deleting the book! The number is invalid.");
        }
        if (!foundBook.isAvailable()) {
            throw new LibraryException("Cannot delete a borrowed book!");
        }
        this.books.remove(foundBook);
    }

    public void addMember(String name, String email) {
        Member member = new Member(name, email);
        this.members.add(member);
    }

    public ArrayList<Member> showMembers() {
        return new ArrayList<>(this.members);
    }

    public ArrayList<Member> searchMember(String search) {
        ArrayList<Member> searchList = new ArrayList<>();
        String searchText = search.toLowerCase();
        for (int i = 0; i < this.members.size(); i++) {
            if (this.members.get(i).getName().toLowerCase().contains(searchText) || this.members.get(i).getEmail().toLowerCase().contains(searchText)) {
                searchList.add(this.members.get(i));
            }
        }
        return searchList;
    }

    public Member findMemberById(long id) {
        for (Member member : members) {
            if (member.getId() == id) {
                return member;
            }
        }
        return null;
    }

    public void editMember(long id, String name, String email) {
        Member foundMember = this.findMemberById(id);
        if (foundMember == null) {
            throw new LibraryException("Member not found!");
        }
        Member.validateParams(name,email);
        foundMember.setName(name);
        foundMember.setEmail(email);
    }

    public void deleteMember(long id) {
        Member foundMember = this.findMemberById(id);
        if (foundMember == null) {
            throw new LibraryException("Member not found!");
        }
        if (!foundMember.borrowedBooks().isEmpty()) {
            throw new LibraryException("The member has borrowed a book!");
        }
        this.members.remove(foundMember);
    }

    public void borrowBook(long bookId, long memberId) {
        Book foundBook = this.findBookById(bookId);
        if (foundBook == null) {
            throw new LibraryException("Book not found!");
        }
        if (!foundBook.isAvailable()) {
            throw new LibraryException("Book is already borrowed!");
        }

        Member foundMember = this.findMemberById(memberId);
        if (foundMember == null) {
            throw new LibraryException("Member not found!");
        }
        foundMember.addBorrowedBooks(foundBook);
        foundBook.setIsAvailable(false);
    }

    public void returnBook(long bookId, long memberId) {
        Book foundBook = this.findBookById(bookId);
        if (foundBook == null) {
            throw new LibraryException("Book not found!");
        }
        if (foundBook.isAvailable()) {
            throw new LibraryException("Book is not borrowed!");
        }
        Member foundMember = this.findMemberById(memberId);
        if (foundMember == null) {
            throw new LibraryException("Member not found!");
        }
        if (!foundMember.hasBorrowedBook(foundBook)) {
            throw new LibraryException("This member didn't borrow this book!");
        }
        foundBook.setIsAvailable(true);
        foundMember.removeBorrowedBook(foundBook);
    }

    public void save() {
        StringBuilder booksText = new StringBuilder();
        StringBuilder membersText = new StringBuilder();
        for (Book b : books) {
            booksText.append(b.toFileLine()).append("\n");
        }
        for (Member m : members) {
            membersText.append(m.toFileLine()).append("\n");
        }
        fileManager.saveText(BOOKS_FILE, booksText.toString());
        fileManager.saveText(MEMBERS_FILE, membersText.toString());
    }

    public void load() {
        this.books.clear();
        this.members.clear();

        File booksFile = new File(BOOKS_FILE);
        if (booksFile.exists()) {
            long maxBookId = 0;
            String booksText = fileManager.loadText(BOOKS_FILE);
            for (String line : booksText.split("\n")) {
                if (line.isBlank()) {
                    continue;
                }
                Book book = Book.fromFileLine(line);
                this.books.add(book);
                maxBookId = Math.max(maxBookId, book.getId());
            }
            IdGenerator.setBookCounter(maxBookId + 1);
        }

        File membersFile = new File(MEMBERS_FILE);
        if (membersFile.exists()) {
            long maxMemberId = 0;
            String membersText = fileManager.loadText(MEMBERS_FILE);
            for (String line : membersText.split("\n")) {
                if (line.isBlank()) {
                    continue;
                }
                Member member = Member.fromFileLine(line);
                for (long bookId : Member.borrowedIdsFromLine(line)) {
                    Book book = this.findBookById(bookId);
                    if (book == null) {
                        throw new LibraryException("Member " + member.getId()
                                + " refers to a missing book: " + bookId);
                    }
                    member.addBorrowedBooks(book);

                }
                this.members.add(member);
                maxMemberId = Math.max(maxMemberId, member.getId());
            }
            IdGenerator.setMemberCounter(maxMemberId + 1);
        }
    }

}
