package edu.temple.bookshelf;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


public class BookDetailsFragment extends Fragment {

    private static final String ARG_BOOK = "param1";
    private Book book;

    TextView title;
    TextView author;

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            book = getArguments().getParcelable(ARG_BOOK);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_book_details, container, false);

        if (layout != null){
            title = layout.findViewById(R.id.Title);
            author = layout.findViewById(R.id.Author);
        }

        if (book != null){
            changeBook(book);
        }

        return layout;
    }

    public void changeBook(Book book){
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
    }
}