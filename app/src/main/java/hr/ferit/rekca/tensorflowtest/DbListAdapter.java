package hr.ferit.rekca.tensorflowtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import hr.ferit.rekca.tensorflowtest.DescriptionDb.DescriptionDbSingleUnit;

public class DbListAdapter extends RecyclerView.Adapter<DbListAdapter.DbListViewHolder> {
    private List<DescriptionDbSingleUnit> mData;
    private Context mContext;
    private OnClickListener listener;

    @NonNull
    @Override
    public DbListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_animlist, viewGroup, false);

        DbListViewHolder vh = new DbListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DbListViewHolder dbListViewHolder, int i) {
        InputStream tempStream = null;
        try {
             tempStream = mContext.openFileInput(mData.get(i).getPicture());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        dbListViewHolder.ivThumbnail.setImageBitmap(BitmapFactory.decodeStream(tempStream));
        dbListViewHolder.tvName.setText(mData.get(i).getName());
        dbListViewHolder.tvGuess.setText(String.valueOf(mData.get(i).getGuess()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class DbListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivThumbnail;
        public TextView tvName;
        public TextView tvGuess;

        public DbListViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvName = itemView.findViewById(R.id.tvName);
            tvGuess = itemView.findViewById(R.id.tvGuess2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(getAdapterPosition());
        }
    }

    public DbListAdapter(List<DescriptionDbSingleUnit> input, Context context, OnClickListener listener){
        mData=input;
        mContext = context;
        this.listener=listener;
    }



}


