package com.securevault.app;

public class VaultItem {

    public String id;
    public String originalName;
    public String encryptedName;
    public String mimeType;
    public long dateAdded;

    // Empty string means the file is in the main/root vault.
    public String folderId;

    public VaultItem(
            String id,
            String originalName,
            String encryptedName,
            String mimeType,
            long dateAdded
    ) {
        this(
                id,
                originalName,
                encryptedName,
                mimeType,
                dateAdded,
                ""
        );
    }

    public VaultItem(
            String id,
            String originalName,
            String encryptedName,
            String mimeType,
            long dateAdded,
            String folderId
    ) {
        this.id = id;
        this.originalName = originalName;
        this.encryptedName = encryptedName;
        this.mimeType = mimeType;
        this.dateAdded = dateAdded;
        this.folderId = folderId == null ? "" : folderId;
    }
}
