package com.stylingandroid.something.oclock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stylingandroid.something.oclock.R;

import java.util.Arrays;
import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final List<String> wordsList;
    private final WordClickHandler clickHandler;

    public static WordsAdapter newInstance(String[] words, WordClickHandler clickHandler) {
        List<String> wordsList = Arrays.asList(words);
        return new WordsAdapter(wordsList, clickHandler);
    }

    WordsAdapter(List<String> wordsList, WordClickHandler clickHandler) {
        this.wordsList = wordsList;
        this.clickHandler = clickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);
        return ViewHolder.newInstance(view, clickHandler);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String word = wordsList.get(position);
        holder.setText(word);
    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }

}
