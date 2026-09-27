package com.securevault.app;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class VaultIndex {

    private static final String INDEX_FILE =
            "vault_index.json";

    private VaultIndex() {
    }

    private static File getIndexFile(Context context) {

        return new File(
                context.getFilesDir(),
                INDEX_FILE
        );
    }

    public static synchronized ArrayList<VaultItem> load(
            Context context
    ) {

        ArrayList<VaultItem> items =
                new ArrayList<>();

        File file =
                getIndexFile(context);

        if (!file.exists()) {
            return items;
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
                return items;
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

                items.add(
                        new VaultItem(
                                object.optString("id"),
                                object.optString(
                                        "originalName"
                                ),
                                object.optString(
                                        "encryptedName"
                                ),
                                object.optString(
                                        "mimeType"
                                ),
                                object.optLong(
                                        "dateAdded"
                                ),
                                object.optString(
                                        "folderId",
                                        ""
                                )
                        )
                );
            }

        } catch (Exception ignored) {
        }

        return items;
    }

    public static synchronized void add(
            Context context,
            VaultItem item
    ) {

        try {

            ArrayList<VaultItem> items =
                    load(context);

            items.add(item);

            save(
                    context,
                    items
            );

        } catch (Exception ignored) {
        }
    }

    public static synchronized void remove(
            Context context,
            String id
    ) {

        try {

            ArrayList<VaultItem> items =
                    load(context);

            ArrayList<VaultItem> remaining =
                    new ArrayList<>();

            for (VaultItem item : items) {

                if (item != null &&
                        item.id != null &&
                        !item.id.equals(id)) {

                    remaining.add(item);
                }
            }

            save(
                    context,
                    remaining
            );

        } catch (Exception ignored) {
        }
    }

    public static synchronized boolean save(
            Context context,
            ArrayList<VaultItem> items
    ) {

        try {

            JSONArray array =
                    new JSONArray();

            for (VaultItem item : items) {

                if (item == null) {
                    continue;
                }

                JSONObject object =
                        new JSONObject();

                object.put(
                        "id",
                        item.id
                );

                object.put(
                        "originalName",
                        item.originalName
                );

                object.put(
                        "encryptedName",
                        item.encryptedName
                );

                object.put(
                        "mimeType",
                        item.mimeType
                );

                object.put(
                        "dateAdded",
                        item.dateAdded
                );

                object.put(
                        "folderId",
                        item.folderId == null
                                ? ""
                                : item.folderId
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
