package com.aikucun.akapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by micker on 2017/7/30.
 */

public class RecycleFooterView implements RecyclerArrayAdapter.ItemView {


    private TextView textView;

    private String text;
    protected LayoutInflater mInflater;


    public RecycleFooterView(LayoutInflater mInflater) {
        this.mInflater = mInflater;
    }

    @Override
    public View onCreateView(ViewGroup parent) {
        View header = mInflater.inflate(R.layout.view_empty, null);
        return header;
    }

    @Override
    public void onBindView(View headerView) {
        textView = (TextView)headerView.findViewById(R.id.empty_text);
        textView.setText(getText());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
