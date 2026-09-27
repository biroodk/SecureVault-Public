package com.securevault.app;

public class VaultFolder {

    public String id;
    public String name;
    public String parentId;
    public long dateCreated;

    public VaultFolder(
            String id,
            String name,
            String parentId,
            long dateCreated
    ) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.dateCreated = dateCreated;
    }
}
