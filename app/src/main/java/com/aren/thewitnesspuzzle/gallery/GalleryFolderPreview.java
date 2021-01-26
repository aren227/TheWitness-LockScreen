package com.aren.thewitnesspuzzle.gallery;

import java.util.List;
import java.util.UUID;

public class GalleryFolderPreview {

    public UUID folderUuid;
    public List<GalleryPuzzlePreview> puzzlePreviews;
    public String name;

    public GalleryFolderPreview(UUID folderUuid, List<GalleryPuzzlePreview> puzzlePreviews, String name) {
        this.folderUuid = folderUuid;
        this.puzzlePreviews = puzzlePreviews;
        this.name = name;
    }

}
