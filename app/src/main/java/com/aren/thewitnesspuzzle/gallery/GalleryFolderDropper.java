package com.aren.thewitnesspuzzle.gallery;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.UUID;

public class GalleryFolderDropper {

    ViewGroup container;
    TextView cancelButton;
    TextView dropButton;

    UUID[] currentFolder;
    PuzzleFactoryManager puzzleFactoryManager;
    PuzzleFactory puzzleFactory;

    public GalleryFolderDropper(final ViewGroup container, TextView cancelButton, TextView dropButton, final UUID[] currentFolder, final PuzzleFactoryManager puzzleFactoryManager, final PuzzleFactory puzzleFactory) {
        this.container = container;
        this.cancelButton = cancelButton;
        this.dropButton = dropButton;
        this.currentFolder = currentFolder;
        this.puzzleFactoryManager = puzzleFactoryManager;
        this.puzzleFactory = puzzleFactory;

        container.setVisibility(View.VISIBLE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.setVisibility(View.GONE);
            }
        });
        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.setVisibility(View.GONE);

                if (!puzzleFactoryManager.isLoaded(puzzleFactory))
                    return;

                puzzleFactory.getConfig().setParentFolderUuid(currentFolder[0]);
                puzzleFactory.getConfig().save();
                puzzleFactoryManager.notifyObservers(); // Not good...
            }
        });
    }

}
