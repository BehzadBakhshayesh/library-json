package library;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonManager {

    private final ObjectMapper mapper = new ObjectMapper();
    private final File file = new File("book.json");

    public void writeBooks(List<Book> books) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, books);

            System.out.println("Books saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Book> readBooks() {
        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<List<Book>>() {}
            );
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}