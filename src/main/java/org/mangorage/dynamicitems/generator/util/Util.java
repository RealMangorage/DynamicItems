package org.mangorage.dynamicitems.generator.util;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Util {
    public static void writeStringToFile(String content, String filePath) throws IOException {
        Path path = Path.of(filePath).toAbsolutePath();
        path.getParent().toFile().mkdirs();
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
        }
    }

    public static void copyFileFromJar(Plugin plugin, String fileName, String destinationPath) throws IOException {
        try (InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream == null) {
                throw new IOException("File not found in JAR: " + fileName);
            }
            Files.createDirectories(Paths.get(destinationPath).getParent());
            Files.copy(inputStream, Paths.get(destinationPath));
        }
    }


    public static void deleteFolder(File folder) throws IOException {
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                deleteFolder(file); // Recursively delete subfolders and files
            }
            folder.delete();  // Delete the empty folder itself
        } else {
            folder.delete();  // Delete the file if it's not a directory
        }
    }


    public static void zipDirectory(String folder, String dest) {
        try {
            var zos = new ZipOutputStream(Files.newOutputStream(Paths.get(dest)));
            zipFolder(Path.of(folder).toFile(), zos);
            zos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipFolder(File folderToZip, ZipOutputStream zos) throws IOException {
        zipFiles(folderToZip, folderToZip, zos);
    }

    private static void zipFiles(File folderToZip, File rootFolder, ZipOutputStream zos) throws IOException {
        for (File file : folderToZip.listFiles()) {
            if (file.isDirectory()) {
                zipFiles(file, rootFolder, zos);
            } else {
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(file);

                // Get the relative path of the current file with respect to the rootFolder
                String relativePath = file.getAbsolutePath().substring(rootFolder.getAbsolutePath().length() + 1);

                // Normalize path separator for ZIP entry
                relativePath = relativePath.replace(File.separator, "/");

                zos.putNextEntry(new ZipEntry(relativePath));

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();
                fis.close();
            }
        }
    }

    public static URI buildURL(String host, int port, String path) {
        try {
            return new URL("http", host, port, path).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getFileHash(File file) throws IOException, NoSuchAlgorithmException {
        // Create MessageDigest instance for MD5
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        int bytesRead;

        // Read file data and update the digest
        while ((bytesRead = fis.read(buffer)) != -1) {
            md.update(buffer, 0, bytesRead);
        }

        // Complete the hash computation
        byte[] hashBytes = md.digest();

        fis.close();

        return hashBytes;
    }

}
