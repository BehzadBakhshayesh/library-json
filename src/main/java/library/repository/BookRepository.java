package library.repository;

import library.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {

    private final static ArrayList<Book> books = new ArrayList<>();

    public Book save(Book book){
        return null;
    }

    public Book update(Book book){
        return null;
    }

    public Book deleteById(String id){
        return null;
    }

    public Optional<Book> findById(String id){
        return null;
    }

    public List<Book> findAll(){
        return null;
    }


    public List<Book> findByLikeTitleOrAuthor(String input){
        return null;
    }

}
