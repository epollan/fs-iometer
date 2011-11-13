package epollan.iometer;

import java.io.*;


public class Driver {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
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
	long bytes = Long.parseLong(args[1]) * (long)Math.pow(10, 9); // GB = 10^9
	
	// Write to/read from temp file
	File tmp = File.createTempFile("data", null, dir);
	try {
	    // Write 1 kB at a time
	    int blocksize = 1000; 
	    if (args.length == 3) {
		blocksize = Integer.parseInt(args[2]);
	    }
	    byte[] data = new byte[blocksize];
	    BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(tmp), 
                                                               blocksize);
	    long bytesWritten = 0;
	    int feedbackInterval = (int)Math.floor((bytes * 1.0) / (blocksize * 20.0));
	    System.out.print("Writing...");
	    long start = System.currentTimeMillis();
	    int loopCount = 0;
	    while (bytesWritten < bytes) {
		os.write(data);
		bytesWritten += blocksize;
		loopCount++;
		if (loopCount % feedbackInterval == 0) {
		    System.out.print(".");
		}
	    }
	    os.flush();
	    os.close();
	    long ms = System.currentTimeMillis() - start;
	    double MBps = (bytesWritten * 1.0) / Math.pow(10, 6) / ((ms * 1.0) / 1000.0);
	    double Mbps = MBps * 8;
	    System.out.format("\nWrote %1$s GB in %2$s seconds (%3$.2f MBps, %4$.2f Mbps)\n", 
			      args[1], (ms / 1000), MBps, Mbps);
	    
	    // Read it back block at a time
	    System.out.print("Reading...");
	    BufferedInputStream is = new BufferedInputStream(new FileInputStream(tmp));
	    start = System.currentTimeMillis();
	    loopCount = 0;
	    while (is.read(data) != -1) {
		loopCount++;
		if (loopCount % feedbackInterval == 0) {
		    System.out.print(".");
		}
	    }
	    is.close();
	    ms = System.currentTimeMillis() - start;
	    MBps = (bytesWritten * 1.0) / Math.pow(10, 6) / ((ms * 1.0) / 1000.0);
	    Mbps = MBps * 8;
	    System.out.format("\nRead %1$s GB in %2$s seconds (%3$.2f MBps, %4$.2f Mbps)\n", 
			      args[1], (ms / 1000), MBps, Mbps);
	}
	finally {
	    tmp.delete();
	}
    }
}
