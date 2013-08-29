package main

import (
	"bufio"
	"time"
	"os"
	"io"
)

func write(f *os.File, args *args) {

	buffer := make([]byte, args.blocksize)
	for i := int64(0); i < args.blocksize; i++ {
		buffer[i] = byte(i % 255)
	}

	bufferedWriter := bufio.NewWriterSize(f, int(args.blocksize))
	feedback := newFeedback("Writing...", args.bytes, args.blocksize)
	
	start := time.Now()
	for bytesWritten := int64(0); bytesWritten < args.bytes; bytesWritten += args.blocksize {
		_, err := bufferedWriter.Write(buffer)
		handleError(err)
		feedback.mark()
	}
	bufferedWriter.Flush()
	logPerformance("Wrote", args.gb, args.bytes, start)
}

func read(f *os.File, args *args) {

	buffer := make([]byte, int(args.blocksize))

	_, err := f.Seek(int64(0), 0)
	handleError(err)
	bufferedReader := bufio.NewReaderSize(f, int(args.blocksize))
	feedback := newFeedback("Reading...", args.bytes, args.blocksize)
	
	start := time.Now()
	for {
		_, err := bufferedReader.Read(buffer)
		if err == io.EOF {
			break
		} else {
			handleError(err)
		}
		feedback.mark()
	}
	logPerformance("Read", args.gb, args.bytes, start)
}
