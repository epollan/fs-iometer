package main

import (
	"fmt"
	"math"
	"time"
)

type feedback struct {
    interval int
	loopCount int
}

func newFeedback(verb string, bytes int64, blocksize int64) *feedback {
	fmt.Printf(verb)
	return &feedback{
        interval: int(math.Floor(float64(bytes) / (float64(blocksize) * 20.0))),
        loopCount: int(0),
	}
}

func (f *feedback) mark() {
	f.loopCount++
	if f.loopCount % f.interval == 0 {
		fmt.Printf(".")
	}
}

func logPerformance(verb string, gb int, bytes int64, start time.Time) {
	duration := time.Now().Sub(start)
    MBps := float64(bytes) / math.Pow10(6) / duration.Seconds()
    fmt.Printf("\n%s %v GB in %.3f seconds (%.2f MBps, %.2f Mbps)\n", verb, gb, duration.Seconds(), MBps, MBps * 8)
}
