package com.securevault.app;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class VaultCrypto {

    private static final int IV_LENGTH = 12;
    private static final int BUFFER_SIZE = 8192;

    private VaultCrypto() {
    }

    public static File getVaultDirectory(Context context) {
        File vault = new File(
                context.getFilesDir(),
                "vault"
        );

        if (!vault.exists()) {
            vault.mkdirs();
        }

        return vault;
    }

    public static File encryptFile(
            Context context,
            File sourceFile,
            byte[] key,
            String outputName
    ) throws Exception {

        File vaultDir = getVaultDirectory(context);

        File encryptedFile =
                new File(vaultDir, outputName + ".vault");

        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher =
                Cipher.getInstance("AES/GCM/NoPadding");

        SecretKeySpec secretKey =
                new SecretKeySpec(key, "AES");

        GCMParameterSpec spec =
                new GCMParameterSpec(128, iv);

        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                spec
        );

        try (
                FileInputStream input =
                        new FileInputStream(sourceFile);

                FileOutputStream output =
                        new FileOutputStream(encryptedFile)
        ) {

            // Store IV at the beginning of the encrypted file.
            output.write(iv);

            byte[] buffer =
                    new byte[BUFFER_SIZE];

            int count;

            while ((count = input.read(buffer)) != -1) {

                byte[] encrypted =
                        cipher.update(
                                buffer,
                                0,
                                count
                        );

                if (encrypted != null) {
                    output.write(encrypted);
                }
            }

            byte[] finalBytes =
                    cipher.doFinal();

            if (finalBytes != null) {
                output.write(finalBytes);
            }

            output.flush();
        }

        return encryptedFile;
    }

    public static File encryptUri(
            Context context,
            Uri sourceUri,
            byte[] key,
            String outputName
    ) throws Exception {

        File vaultDir = getVaultDirectory(context);

        File encryptedFile =
                new File(vaultDir, outputName + ".vault");

        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher =
                Cipher.getInstance("AES/GCM/NoPadding");

        SecretKeySpec secretKey =
                new SecretKeySpec(key, "AES");

        GCMParameterSpec spec =
                new GCMParameterSpec(128, iv);

        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                spec
        );

        android.content.ContentResolver resolver =
                context.getContentResolver();

        try (
                java.io.InputStream input =
                        resolver.openInputStream(sourceUri);

                FileOutputStream output =
                        new FileOutputStream(encryptedFile)
        ) {

            if (input == null) {
                throw new Exception(
                        "Unable to read selected file."
                );
            }

            output.write(iv);

            byte[] buffer =
                    new byte[BUFFER_SIZE];

            int count;

            while ((count = input.read(buffer)) != -1) {

                byte[] encrypted =
                        cipher.update(
                                buffer,
                                0,
                                count
                        );

                if (encrypted != null) {
                    output.write(encrypted);
                }
            }

            byte[] finalBytes =
                    cipher.doFinal();

            if (finalBytes != null) {
                output.write(finalBytes);
            }

            output.flush();
        }

        if (!encryptedFile.exists()
                || encryptedFile.length() <= IV_LENGTH) {

            if (encryptedFile.exists()) {
                encryptedFile.delete();
            }

            throw new Exception(
                    "Encrypted vault file was not created."
            );
        }

        return encryptedFile;
    }

    public static void decryptFile(
            File encryptedFile,
            File destinationFile,
            byte[] key
    ) throws Exception {

        try (
                FileInputStream input =
                        new FileInputStream(encryptedFile)
        ) {

            byte[] iv =
                    new byte[IV_LENGTH];

            int read =
                    input.read(iv);

            if (read != IV_LENGTH) {
                throw new Exception(
                        "Invalid vault file."
                );
            }

            Cipher cipher =
                    Cipher.getInstance(
                            "AES/GCM/NoPadding"
                    );

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            key,
                            "AES"
                    );

            GCMParameterSpec spec =
                    new GCMParameterSpec(
                            128,
                            iv
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    spec
            );

            try (
                    FileOutputStream output =
                            new FileOutputStream(
                                    destinationFile
                            )
            ) {

                byte[] buffer =
                        new byte[BUFFER_SIZE];

                int count;

                while ((count = input.read(buffer)) != -1) {

                    byte[] decrypted =
                            cipher.update(
                                    buffer,
                                    0,
                                    count
                            );

                    if (decrypted != null) {
                        output.write(decrypted);
                    }
                }

                byte[] finalBytes =
                        cipher.doFinal();

                if (finalBytes != null) {
                    output.write(finalBytes);
                }

                output.flush();
            }
        }
    }
}
