package org.mangorage.ftp;




import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileSharing extends NanoHTTPD {

    private final String filesDirectory = Path.of("files").toString();

    public FileSharing(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        System.out.println(uri);
        if (uri.startsWith("/files/")) {
            String filePath = Path.of("files").resolve(uri.substring("/files/".length())).toString();
            if (filePath.startsWith("/"))
                filePath = filePath.replaceFirst("/", "");
            System.out.println(filePath);
            try {
                byte[] fileContent = readFile(filePath);
                if (fileContent != null) {
                    InputStream inputStream = new ByteArrayInputStream(fileContent);
                    return newChunkedResponse(Response.Status.OK, "application/octet-stream", inputStream);
                } else {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File not found");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Internal server error");
            }
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found");
        }
    }

    private byte[] readFile(String filePath) throws IOException {
        Path path = Path.of(filePath).toAbsolutePath();
        System.out.println(filePath + " ->");
        if (Files.exists(path)) {
            return Files.readAllBytes(path);
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        var server = new FileSharing(1080);
        server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("HTTP Server started on port " + port);
    }
}
