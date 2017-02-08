package ashutosh.nanodegree.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ashutosh.nanodegree.beans.Movies;
import ashutosh.nanodegree.network.WebUtils;
import ashutosh.nanodegree.popularmovies.R;

/**
 * Created by ashutosh on 9/9/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemHolder> {

    private final Context context;
    private List<Movies> itemsName;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;
    private int layout;

    public RecyclerViewAdapter(Context context, List<Movies> itemsName, int layout) {
        layoutInflater = LayoutInflater.from(context);
        this.layout = layout;
        this.context = context;
        this.itemsName = itemsName;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerViewAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(layout, parent, false);
        return new ItemHolder(itemView, this);

    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ItemHolder holder, int position) {

        holder.setItem(itemsName.get(position), context, position);
    }

    @Override
    public int getItemCount() {
        return itemsName.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void add(int location, Movies iName) {
        itemsName.add(location, iName);
        notifyItemInserted(location);
    }

    public void addArray(ArrayList<Movies> items) {
        itemsName.addAll(items);
    }

    public void addArray(Movies[] items) {
        itemsName.addAll(Arrays.asList(items));
    }

    public void remove(int location) {
        if (location >= itemsName.size())
            return;

        itemsName.remove(location);
        notifyItemRemoved(location);
    }

    public interface OnItemClickListener {
        void onItemClick(ItemHolder item, Movies movie, int position) throws JSONException;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivItem;
        private RecyclerViewAdapter parent;
        private Movies movieData;

        public ItemHolder(View itemView, RecyclerViewAdapter parent) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.parent = parent;
            ivItem = (ImageView) itemView;

        }

        public void setItem(Movies itemObject, Context context, int position) {
            /**
             * Can you please give an opinion about glide vs Picasso.
             */
            this.movieData = itemObject;
            Glide.with(context).load(WebUtils.imageBaseUrl + itemObject.getPosterPath()).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivItem);
            // Picasso.with(context).load(WebUtils.imageBaseUrl + itemObject.getPosterPath()).placeholder(R.drawable.placeholder).fit().into(ivItem);
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = parent.getOnItemClickListener();
            if (listener != null) {
                try {
                    listener.onItemClick(this, movieData, getPosition());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}