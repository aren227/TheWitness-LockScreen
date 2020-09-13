package com.aren.thewitnesspuzzle.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
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

    private List<PuzzleFactory> order;

    public PuzzleOrderAdapter(Context context, PuzzleFactoryManager puzzleFactoryManager){
        this.context = context;
        this.puzzleFactoryManager = puzzleFactoryManager;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        order = new ArrayList<>();
    }

    public void addPuzzle(PuzzleFactory factory){
        order.add(factory);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ordergridview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PuzzleFactory factory = order.get(position);

        holder.imageView.setImageBitmap(factory.getThumbnailCache());
    }

    @Override
    public int getItemCount() {
        return order.size();
    }

    @Override
    public void onItemMove(int fromPos, int targetPos) {
        Collections.swap(order, fromPos, targetPos);
        notifyItemMoved(fromPos, targetPos);
    }

    @Override
    public void onItemDismiss(int pos) {
        order.remove(pos);
        notifyItemRemoved(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
