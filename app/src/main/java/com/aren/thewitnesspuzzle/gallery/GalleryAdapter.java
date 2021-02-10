package com.aren.thewitnesspuzzle.gallery;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.dialog.FolderDialog;
import com.aren.thewitnesspuzzle.dialog.NewFolderDialog;
import com.aren.thewitnesspuzzle.dialog.NewPuzzleDialog;
import com.aren.thewitnesspuzzle.dialog.PuzzleFactoryDialog;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private PuzzleFactoryManager puzzleFactoryManager;

    private List<Object> previews;

    private OnPreviewClick onPreviewClick;

    private UUID[] currentFolder;

    public GalleryAdapter(Context context, PuzzleFactoryManager puzzleFactoryManager, OnPreviewClick onPreviewClick, UUID[] currentFolder) {
        this.context = context;
        this.puzzleFactoryManager = puzzleFactoryManager;
        this.onPreviewClick = onPreviewClick;
        this.currentFolder = currentFolder;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        previews = new ArrayList<>();
    }

    public void addPreview(GalleryPuzzlePreview preview) {
        previews.add(preview);
    }

    public void addPreview(GalleryFolderPreview preview) {
        previews.add(preview);
    }

    public void clearPreviews() {
        previews.clear();
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= previews.size())
            return 2;
        if (previews.get(position) instanceof GalleryPuzzlePreview)
            return 0;
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = inflater.inflate(R.layout.gridview_layout, parent, false);
            return new PuzzleViewHolder(view);
        } else if (viewType == 1) {
            view = inflater.inflate(R.layout.gallery_folder_layout, parent, false);
            return new FolderViewHolder(view);
        }
        view = inflater.inflate(R.layout.new_button_layout, parent, false);
        return new AddButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position >= previews.size()) {
            AddButtonViewHolder viewHolder = (AddButtonViewHolder) holder;

            viewHolder.addPuzzleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewPuzzleDialog dialog = new NewPuzzleDialog(context, currentFolder[0]);
                    dialog.show();
                }
            });

            if (currentFolder[0] == PuzzleFactoryManager.rootFolderUuid || (puzzleFactoryManager.getFolder(currentFolder[0]) != null && puzzleFactoryManager.getFolder(currentFolder[0]).getDepth() < 2)){
                viewHolder.addFolderImageView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.addFolderImageView.setVisibility(View.GONE);
            }

            viewHolder.addFolderImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final UUID parent = currentFolder[0];
                    NewFolderDialog dialog = new NewFolderDialog(context, puzzleFactoryManager, parent);
                    dialog.show();
                }
            });
        } else if (previews.get(position) instanceof GalleryPuzzlePreview) {
            final GalleryPuzzlePreview preview = (GalleryPuzzlePreview) previews.get(position);
            PuzzleViewHolder viewHolder = (PuzzleViewHolder) holder;

            viewHolder.imageView.setImageBitmap(preview.bitmap);
            viewHolder.imageView.setClipToOutline(true);

            if (puzzleFactoryManager.getLastViewedProfile().isActivated(preview.puzzleFactory)) {
                viewHolder.imageView.setColorFilter(null);
                viewHolder.imageView.setImageAlpha(255);
                viewHolder.outlineView.setVisibility(View.VISIBLE);
            } else {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
                viewHolder.imageView.setColorFilter(cf);
                viewHolder.imageView.setImageAlpha(128);
                viewHolder.outlineView.setVisibility(View.INVISIBLE);
            }

            viewHolder.textView.setText(preview.name);
            if (preview.getDifficulty() != null)
                viewHolder.textView.setTextColor(preview.getDifficulty().getColor());

            viewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPreviewClick.onClick(preview);
                }
            });

            viewHolder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PuzzleFactoryDialog dialog = new PuzzleFactoryDialog(context, preview.puzzleFactory, currentFolder[0]);
                    dialog.show();
                    return true;
                }
            });
        } else {
            final GalleryFolderPreview preview = (GalleryFolderPreview) previews.get(position);
            FolderViewHolder viewHolder = (FolderViewHolder) holder;

            PuzzleFactoryManager.Profile lastProfile = puzzleFactoryManager.getLastViewedProfile();

            for (int i = 0; i < 4; i++) {
                ImageView imageView = viewHolder.imageViewList.get(i);
                View outlineView = viewHolder.outlineViewList.get(i);
                if (i < preview.puzzlePreviews.size()) {
                    imageView.setImageBitmap(preview.puzzlePreviews.get(i).bitmap);
                    imageView.setClipToOutline(true);

                    if (lastProfile.isActivated(preview.puzzlePreviews.get(i).puzzleFactory)) {
                        imageView.setColorFilter(null);
                        imageView.setImageAlpha(255);
                        outlineView.setVisibility(View.VISIBLE);
                    } else {
                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);
                        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
                        imageView.setColorFilter(cf);
                        imageView.setImageAlpha(128);
                        outlineView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    viewHolder.imageViewList.get(i).setImageBitmap(null);
                    viewHolder.imageViewList.get(i).setColorFilter(null);
                }
            }

            viewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPreviewClick.onClick(preview);
                }
            });

            viewHolder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    UUID folderUuid = preview.folderUuid;
                    PuzzleFactoryManager.Folder folder = puzzleFactoryManager.getFolder(folderUuid);
                    if (folder != null) {
                        if (folder.isImmutable()) {
                            Toast.makeText(context, "This folder cannot be modified.", Toast.LENGTH_SHORT).show();
                        } else {
                            FolderDialog dialog = new FolderDialog(context, puzzleFactoryManager, folder);
                            dialog.show();
                        }
                    }
                    return true;
                }
            });

            viewHolder.textView.setText(preview.name);
        }
    }

    @Override
    public int getItemCount() {
        return previews.size() + 1;
    }

    public List<Object> getItems() {
        return previews;
    }

    public static class PuzzleViewHolder extends RecyclerView.ViewHolder {
        ViewGroup root;
        ImageView imageView;
        TextView textView;
        View outlineView;

        public PuzzleViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.preview_root);
            imageView = itemView.findViewById(R.id.puzzle_preview);
            textView = itemView.findViewById(R.id.puzzle_name);
            outlineView = itemView.findViewById(R.id.puzzle_selected);
        }
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        ViewGroup root;
        List<ImageView> imageViewList = new ArrayList<>();
        List<View> outlineViewList = new ArrayList<>();
        TextView textView;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.preview_root);

            imageViewList.add((ImageView) itemView.findViewById(R.id.puzzle_preview_1));
            imageViewList.add((ImageView) itemView.findViewById(R.id.puzzle_preview_2));
            imageViewList.add((ImageView) itemView.findViewById(R.id.puzzle_preview_3));
            imageViewList.add((ImageView) itemView.findViewById(R.id.puzzle_preview_4));

            outlineViewList.add(itemView.findViewById(R.id.puzzle_selected_1));
            outlineViewList.add(itemView.findViewById(R.id.puzzle_selected_2));
            outlineViewList.add(itemView.findViewById(R.id.puzzle_selected_3));
            outlineViewList.add(itemView.findViewById(R.id.puzzle_selected_4));

            textView = itemView.findViewById(R.id.folder_name);
        }
    }

    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        ImageView addPuzzleImageView, addFolderImageView;

        public AddButtonViewHolder(@NonNull View itemView) {
            super(itemView);

            addPuzzleImageView = itemView.findViewById(R.id.add_puzzle);
            addFolderImageView = itemView.findViewById(R.id.add_folder);
        }
    }
}
