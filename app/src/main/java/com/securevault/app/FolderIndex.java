package com.securevault.app;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FolderIndex {

    private static final String INDEX_FILE =
            "vault_folders.json";

    private FolderIndex() {
    }

    private static File getIndexFile(Context context) {
        return new File(
                context.getFilesDir(),
                INDEX_FILE
        );
    }

    public static synchronized ArrayList<VaultFolder> load(
            Context context
    ) {

        ArrayList<VaultFolder> folders =
                new ArrayList<>();

        File file =
                getIndexFile(context);

        if (!file.exists()) {
            return folders;
        }

        try {

            FileInputStream input =
                    new FileInputStream(file);

            byte[] data =
                    new byte[(int) file.length()];

            int read =
                    input.read(data);

            input.close();

            if (read <= 0) {
                return folders;
            }

            String json =
                    new String(
                            data,
                            StandardCharsets.UTF_8
                    );

            JSONArray array =
                    new JSONArray(json);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);

                folders.add(
                        new VaultFolder(
                                object.optString("id"),
                                object.optString("name"),
                                object.optString("parentId"),
                                object.optLong("dateCreated")
                        )
                );
            }

        } catch (Exception ignored) {
        }

        return folders;
    }

    public static synchronized void add(
            Context context,
            VaultFolder folder
    ) {

        ArrayList<VaultFolder> folders =
                load(context);

        folders.add(folder);

        save(
                context,
                folders
        );
    }

    public static synchronized void remove(
            Context context,
            String id
    ) {

        ArrayList<VaultFolder> folders =
                load(context);

        ArrayList<VaultFolder> remaining =
                new ArrayList<>();

        for (VaultFolder folder : folders) {

            if (folder != null &&
                    folder.id != null &&
                    !folder.id.equals(id)) {

                remaining.add(folder);
            }
        }

        save(
                context,
                remaining
        );
    }

    public static synchronized boolean save(
            Context context,
            ArrayList<VaultFolder> folders
    ) {

        try {

            JSONArray array =
                    new JSONArray();

            for (VaultFolder folder : folders) {

                if (folder == null) {
                    continue;
                }

                JSONObject object =
                        new JSONObject();

                object.put(
                        "id",
                        folder.id
                );

                object.put(
                        "name",
                        folder.name
                );

                object.put(
                        "parentId",
                        folder.parentId
                );

                object.put(
                        "dateCreated",
                        folder.dateCreated
                );

                array.put(object);
            }

            File file =
                    getIndexFile(context);

            File tempFile =
                    new File(
                            file.getParentFile(),
                            file.getName() + ".tmp"
                    );

            byte[] data =
                    array.toString(2)
                            .getBytes(
                                    StandardCharsets.UTF_8
                            );

            FileOutputStream output =
                    new FileOutputStream(tempFile);

            output.write(data);
            output.flush();
            output.close();

            if (!tempFile.exists()
                    || tempFile.length() != data.length) {
                tempFile.delete();
                return false;
            }

            if (file.exists()
                    && !file.delete()) {
                tempFile.delete();
                return false;
            }

            if (!tempFile.renameTo(file)) {
                tempFile.delete();
                return false;
            }

            return file.exists()
                    && file.length() == data.length;

        } catch (Exception e) {
            return false;
        }
    }
}
