package Problem2;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class Downloader {
    private final InputStream in;
    private final OutputStream out;
    private final List<ProgressListener> listeners = new ArrayList<>();

    public Downloader(URL url, String outputFilename) throws IOException {
        in = url.openConnection().getInputStream();
        out = new FileOutputStream(outputFilename);
    }

    public synchronized void addListener(ProgressListener listener) {
        listeners.add(listener);
    }

    private synchronized void updateProgress(int total) {
        for (ProgressListener listener : listeners) {
            listener.onProgress(total);
        }
    }

    public void run() throws IOException {
        int n;
        int total = 0;
        byte[] buffer = new byte[1024];

        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
            total += n;
            updateProgress(total);
        }
        out.flush();
    }
}