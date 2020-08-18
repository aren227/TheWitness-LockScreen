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

public class GalleryAdapter extends BaseAdapter {

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

    @Override
    public int getCount() {
        return previews.size();
    }

    @Override
    public Object getItem(int position) {
        return previews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_layout, null);
        }

        final GalleryPreview preview = previews.get(position);

        ImageView imageView = convertView.findViewById(R.id.puzzle_preview);
        imageView.setImageBitmap(preview.bitmap);
        imageView.setClipToOutline(true);

        if(puzzleFactoryManager.isActiavted(preview.puzzleFactory)){
            imageView.setColorFilter(null);
            imageView.setImageAlpha(255);
        }
        else{
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            imageView.setColorFilter(cf);
            imageView.setImageAlpha(128);
        }

        TextView textView = convertView.findViewById(R.id.puzzle_name);
        textView.setText(preview.name);
        if(preview.getDifficulty() != null) textView.setTextColor(preview.getDifficulty().getColor());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                puzzleFactoryManager.setActivated(preview.puzzleFactory, !puzzleFactoryManager.isActiavted(preview.puzzleFactory));
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
