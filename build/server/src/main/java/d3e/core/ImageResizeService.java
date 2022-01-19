package d3e.core;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ImageResizeService implements Runnable {

    private static final long SLEEP_TIME = 1_000;

    private class ResizeJob {
        String fileName;
        int width;
        int height;
        D3EResourceHandler handler;

        ResizeJob(String fileName, int width, int height, D3EResourceHandler handler) {
            this.fileName = fileName;
            this.width = width;
            this.height = height;
            this.handler = handler;
        }
    }

    private Queue<ResizeJob> jobs = new ConcurrentLinkedQueue<>();

    public void resize(String fileName, int width, int height, D3EResourceHandler handler) {
        pushJob(fileName, width, height, handler);
    }

    @Override
    public void run() {
        while (true) {
            ResizeJob job = popJob();
            if (job == null) {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                }
            } else {
                if (!resizeAndSave(job)) {
                    pushJob(job);
                }
            }
        }
    }

    private boolean resizeAndSave(ResizeJob job) {
        // Do resize operation on the file according to width and height
        /*
         * 1. Get Resource for this file name from resourceHandler 2. Do resize
         * operation based on width and height 3. Save the resulting file using
         * resourceHandler
         */
        // Get the original
        Resource resource = job.handler.get(job.fileName);
        try {
            File file = resource.getFile();
            // Do resize operation here.
            DFile dFile = new DFile();
            dFile.setMimeType(FileUtils.detectMimeType(file));
            dFile.setId(FileUtils.getResizedName(job.fileName, job.width, job.height));
            job.handler.save(dFile);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void pushJob(String fileName, int width, int height, D3EResourceHandler handler) {
        pushJob(new ResizeJob(fileName, width, height, handler));
    }

    private synchronized void pushJob(ResizeJob job) {
        this.jobs.add(job);
    }

    private synchronized ResizeJob popJob() {
        return this.jobs.poll();
    }
}
