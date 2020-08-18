package com.aren.thewitnesspuzzle;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private PuzzleFactoryManager puzzleFactoryManager;

    private List<GalleryPreview> previews;

    public GalleryAdapter(Context context, PuzzleFactoryManager puzzleFactoryManager){
        this.context = context;
        this.puzzleFactoryManager = puzzleFactoryManager;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        previews = new ArrayList<>();
    }

    public void addPreview(GalleryPreview preview){
        previews.add(preview);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.gridview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final GalleryPreview preview = previews.get(position);

        holder.imageView.setImageBitmap(preview.bitmap);
        holder.imageView.setClipToOutline(true);

        if(puzzleFactoryManager.isActiavted(preview.puzzleFactory)){
            holder.imageView.setColorFilter(null);
            holder.imageView.setImageAlpha(255);
        }
        else{
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            holder.imageView.setColorFilter(cf);
            holder.imageView.setImageAlpha(128);
        }

        holder.textView.setText(preview.name);
        if(preview.getDifficulty() != null) holder.textView.setTextColor(preview.getDifficulty().getColor());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                puzzleFactoryManager.setActivated(preview.puzzleFactory, !puzzleFactoryManager.isActiavted(preview.puzzleFactory));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return previews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.puzzle_preview);
            textView = itemView.findViewById(R.id.puzzle_name);
        }
    }
}
