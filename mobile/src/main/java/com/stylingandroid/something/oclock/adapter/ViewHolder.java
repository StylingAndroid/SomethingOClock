package com.stylingandroid.something.oclock.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    static ViewHolder newInstance(View itemView, WordClickHandler clickHandler) {
        TextView textView = (TextView) itemView.findViewById(android.R.id.text1);
        return new ViewHolder(itemView, textView, clickHandler);
    }

    ViewHolder(View itemView, TextView textView, WordClickHandler clickHandler) {
        super(itemView);
        this.textView = textView;
        itemView.setOnClickListener(new ClickListener(clickHandler));
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    private final class ClickListener implements View.OnClickListener {
        private final WordClickHandler clickHandler;

        private ClickListener(WordClickHandler clickHandler) {
            this.clickHandler = clickHandler;
        }

        @Override
        public void onClick(View v) {
            clickHandler.wordSelected((String) textView.getText());
        }
    }
}
