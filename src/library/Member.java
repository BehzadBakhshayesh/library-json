package library;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Member {
    private ArrayList<Book> borrowedBooks = new ArrayList<>();
    private final long id;
    private String name;
    private String email;

    public Member(String name, String email) {
        validateParams(name, email);
        this.id = IdGenerator.generateMemberId();
        this.name = name;
        this.email = email;
    }

    public Member(long id, String name, String email) {
        validateParams(name, email);
        this.id = id;
        this.name = name;
        this.email = email;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new LibraryException("Name can not be blank");
        }
        if (name.contains("|")) {
            throw new LibraryException("The '|' character is not allowed");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new LibraryException("Email can not be blank");
        }
        if (email.contains("|")) {
            throw new LibraryException("The '|' character is not allowed");
        }
        if (!(email.contains("@") && email.contains("."))) {
            throw new LibraryException("Email is not valid");
        }
    }

    public static void validateParams(String name, String email) {
        validateName(name);
        validateEmail(email);
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }


    public ArrayList<Book> borrowedBooks() {
        return new ArrayList<>(this.borrowedBooks);
    }

    public void addBorrowedBooks(Book book) {
        if (borrowedBooks.contains(book)){
            throw new LibraryException("This member already borrowed this book!");
        }
        this.borrowedBooks.add(book);
    }

    public void removeBorrowedBook(Book book) {
        this.borrowedBooks.remove(book);
    }

    public boolean hasBorrowedBook(Book book) {
        return borrowedBooks.contains(book);
    }

    public String toFileLine() {
        String ids = this.borrowedBooks.stream()
                .map(book -> String.valueOf(book.getId()))
                .collect(Collectors.joining(","));
        return this.id + "|" + this.name + "|" + this.email + "|" + ids;
    }

    public static Member fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
            throw new LibraryException("Invalid member line: " + line);
        }
        try {
            return new Member(
                    Long.parseLong(parts[0].trim()),
                    parts[1].trim(),
                    parts[2].trim()
            );
        } catch (NumberFormatException e) {
            throw new LibraryException("Invalid number in member line: " + line);
        }
    }

    public static ArrayList<Long> borrowedIdsFromLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
            throw new LibraryException("Invalid member line: " + line);
        }
        ArrayList<Long> ids = new ArrayList<>();
        String last = parts[3].trim();
        if (last.isEmpty()) {
            return ids;
        }
        try {
            for (String s : last.split(",")) {
                ids.add(Long.parseLong(s.trim()));
            }
        } catch (NumberFormatException e) {
            throw new LibraryException("Invalid book id in member line: " + line);
        }
        return ids;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + " | Name: " + this.name + " | Email: " + this.email + " | Borrowed Books: " + borrowedBooks.stream()
                .map(book -> book.getTitle())
                .collect(Collectors.joining(", "));
    }
}
