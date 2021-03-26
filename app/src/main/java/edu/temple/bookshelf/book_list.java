package edu.temple.bookshelf;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link book_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class book_list extends Fragment {

    private static final String ARG_BOOK_LIST = "param1";
    private BookList bookList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public book_list() {
    }


    public static book_list newInstance(BookList bookList) {
        book_list fragment = new book_list();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOK_LIST, bookList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            bookList = getArguments().getParcelable(ARG_BOOK_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ListView listView = (ListView) inflater.inflate(R.layout.fragment_book_list2, container, false);

        listView.setAdapter(new BookAdapter(getActivity(), android.R.layout.simple_list_item_1, bookList.getList()));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return listView;
    }
}