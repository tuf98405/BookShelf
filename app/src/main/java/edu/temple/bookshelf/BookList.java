package edu.temple.bookshelf;
import java.util.ArrayList;

public class BookList{
    ArrayList<Book> bookList;
    public BookList(){
        bookList = new ArrayList<Book>();
    }

    public void add(Book book){
        bookList.add(book);
    }

    public void remove(Book book){
        bookList.remove(book);
    }

    public Book get(int index){
        return bookList.get(index);
    }

    public int size(){
        return bookList.size();
    }
}
