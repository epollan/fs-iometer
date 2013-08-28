package epollan.iometer;

import com.google.common.base.Stopwatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;


public class Driver {

    public static void main(String[] args)
            throws Exception {
        if (args.length < 2) {
            System.err.format("Usage:  java -jar fs-iometer*.jar <path> <# GB> [blocksize in bytes]\n");
            return;
        }
        // Parse args
        String path = args[0];
        File dir = new File(path);
        if (!dir.exists()) {
            System.err.println("Invalid path: " + path);
            return;
        }
        long bytes = Long.parseLong(args[1]) * (long) Math.pow(10, 9); // GB = 10^9

        // Write to/read from temp file
        File tmp = File.createTempFile("data", null, dir);
        tmp.deleteOnExit();

        // Write 32 kB at a time, unless directed otherwise
        int blocksize = 32768;
        if (args.length == 3) {
            blocksize = Integer.parseInt(args[2]);
        }
        byte[] data = new byte[blocksize];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % Byte.MAX_VALUE);
        }

        // Provide feedback every 5%
        int feedbackInterval = (int) Math.floor((bytes * 1.0) / (blocksize * 20.0));
        int loopCount = 0;

        System.out.print("Writing...");
        ByteBuffer buffer = ByteBuffer.wrap(data);
        FileChannel channel = new FileOutputStream(tmp).getChannel();
        long bytesWritten = 0;
        Stopwatch watch = new Stopwatch();
        watch.start();
        while (bytesWritten < bytes) {
            channel.write(buffer);
            buffer.rewind();
            bytesWritten += blocksize;
            if (++loopCount % feedbackInterval == 0) {
                System.out.print(".");
            }
        }
        channel.force(true);
        channel.close();

        logPerformance("Wrote", bytesWritten, watch.stop());

        // Read it back block at a time
        System.out.print("Reading...");
        channel = new FileInputStream(tmp).getChannel();
        loopCount = 0;
        int readCount;
        buffer.clear();
        watch.reset().start();
        while ((readCount = channel.read(buffer)) != -1) {
            buffer.flip();
            buffer.get(data, 0, readCount);
            buffer.clear();
            if (++loopCount % feedbackInterval == 0) {
                System.out.print(".");
            }
        }

        logPerformance("Read", bytesWritten, watch.stop());
    }

    private static void logPerformance(String verb, long bytes, Stopwatch watch) {
        long GB = (long)(bytes / Math.pow(10, 9));
        // get integer number of MS and find (potentially) fractional seconds
        double seconds = watch.elapsed(TimeUnit.MILLISECONDS) / 1000.0;
        double MBps = (bytes * 1.0) / Math.pow(10, 6) / seconds;
        System.out.format("%n%1s %2$s GB in %3$s seconds (%4$.2f MBps, %5$.2f Mbps)%n",
                          verb, GB, seconds, MBps, MBps * 8);

    }
}
