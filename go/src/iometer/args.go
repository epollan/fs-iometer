package main

import (
	"flag"
	"fmt"
	"log"
	"math"
	"os"
	"strconv"
)

type args struct {
	gb        int    // GB to read/write
	bytes     int64  // bytes to read/write
	dir       string // directory in which data should be written/read
	blocksize int64  // blocksize for buffered reads/writes
}

func ParseArgs() *args {
	var blocksize = flag.Int64("blocksize", 32768, "Read/write blocksize, defaults to 32KB")
	flag.Usage = usage
	flag.Parse()
	positional := flag.Args()

	if len(positional) < 2 {
		usage()
	}

	info, err := os.Stat(positional[0])
	handleError(err)
	if !info.IsDir() {
		fail(positional[0] + " is not a directory")
	}

	gb, err := strconv.ParseUint(positional[1], 10, 32)
	handleError(err)

	return &args{
		gb:        int(gb),
		bytes:     int64(uint64(math.Pow10(9)) * uint64(gb)),
		dir:       positional[0],
		blocksize: *blocksize,
	}
}

func usage() {
	fmt.Fprintf(os.Stderr, "Usage:  iometer [options] <dir> <# GB>\n")
	flag.PrintDefaults()
	os.Exit(2)
}

func handleError(err error) {
	if err != nil {
		log.Fatal(err)
	}
}

func fail(msg string) {
	log.Fatal(msg)
}
