package edu.temple.bookshelf;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends ArrayAdapter {

    Context context;
    ArrayList<Book> bookArray;

    public BookAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        this.bookArray = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bookArray.size();
    }

    @Override
    public Object getItem(int position) {
        return bookArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TextView textView;

        if (convertView == null) {
            textView = new TextView(parent.getContext());
        } else {
            textView = (TextView) convertView;
        }

        textView.setText(((Book)(getItem(position))).getTitle());
        return textView;
    }
}
