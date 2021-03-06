package com.aren.thewitnesspuzzle.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.activity.GalleryActivity;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PuzzleOrderAdapter extends RecyclerView.Adapter<PuzzleOrderAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperAdapter {

    private Context context;
    private LayoutInflater inflater;
    private PuzzleFactoryManager puzzleFactoryManager;

    private List<PuzzleFactory> sequence;

    private OnUpdate onUpdate;

    public PuzzleOrderAdapter(Context context, PuzzleFactoryManager puzzleFactoryManager, OnUpdate onUpdate){
        this.context = context;
        this.puzzleFactoryManager = puzzleFactoryManager;
        this.onUpdate = onUpdate;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sequence = new ArrayList<>();
    }

    public void addPuzzle(PuzzleFactory factory){
        sequence.add(factory);
        onUpdate.onUpdate();
    }

    public void setSequence(List<PuzzleFactory> order){
        this.sequence = order;
    }

    public List<PuzzleFactory> getSequence(){
        return sequence;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ordergridview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PuzzleFactory factory = sequence.get(position);

        if(factory.getThumbnailCache() == null){
            holder.imageView.setImageBitmap(GalleryActivity.getNotLoadedBitmap());
            factory.setOnPreviewRendered(new Runnable() {
                @Override
                public void run() {
                    holder.imageView.setImageBitmap(factory.getThumbnailCache());
                }
            });
        }
        else{
            holder.imageView.setImageBitmap(factory.getThumbnailCache());
        }
        holder.textView.setText(factory.getName());
    }

    @Override
    public int getItemCount() {
        return sequence.size();
    }

    @Override
    public void onItemMove(int fromPos, int targetPos) {
        Collections.swap(sequence, fromPos, targetPos);
        notifyItemMoved(fromPos, targetPos);
        onUpdate.onUpdate();
    }

    @Override
    public void onItemDismiss(int pos) {
        sequence.remove(pos);
        notifyItemRemoved(pos);
        onUpdate.onUpdate();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.name);
        }
    }
}
