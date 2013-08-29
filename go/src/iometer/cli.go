package main

import (
	"os"
	"io/ioutil"
)

func main() {
	var args = ParseArgs()

	file, err := ioutil.TempFile(args.dir, "data")
	handleError(err)

	write(file, args)
	read(file, args)

	defer func() {
		err := os.Remove(file.Name())
		handleError(err)
	}()
}

