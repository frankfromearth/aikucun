package com.aikucun.akapp.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aikucun.akapp.api.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表 Adapter 基类
 * Created by jarry on 16/6/3.
 */
public class ListBaseAdapter<T extends Entity> extends BaseAdapter
{
    // 列表数据
    protected List<T> mDatas = new ArrayList<T>();

    private LayoutInflater mInflater;

    protected LayoutInflater getLayoutInflater(Context context)
    {
        if (mInflater == null)
        {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return mInflater;
    }

    public int getDataSize()
    {
        return mDatas.size();
    }

    @Override
    public int getCount()
    {
        return getDataSize();
    }

    @Override
    public T getItem(int arg0)
    {
        if (mDatas.size() > arg0)
        {
            return mDatas.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0)
    {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getRealView(position, convertView, parent);
    }

    protected View getRealView(int position, View convertView, ViewGroup parent)
    {
        return null;
    }

    public void setData(List<T> data)
    {
        mDatas = data;
        notifyDataSetChanged();
    }

    public List<T> getData()
    {
        return mDatas == null ? (mDatas = new ArrayList<T>()) : mDatas;
    }

    public void addData(List<T> data)
    {
        if (mDatas != null && data != null && !data.isEmpty())
        {
            mDatas.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addItem(T obj)
    {
        if (mDatas != null)
        {
            mDatas.add(obj);
        }
        notifyDataSetChanged();
    }

    public void removeItem(Object obj)
    {
        mDatas.remove(obj);
        notifyDataSetChanged();
    }

    public void clear()
    {
        mDatas.clear();
        notifyDataSetChanged();
    }

}
