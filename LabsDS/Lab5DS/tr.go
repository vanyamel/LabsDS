package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

const (
	arrayLength = 5
)

var (
	arrays = [3][]int{
		{1, 2, 3, 4, 5},
		{3, 3, 3, 3, 3},
		{5, 2, 3, 2, 3},
	}
	mu    sync.Mutex
	wg    sync.WaitGroup
	done  bool
	equal int
)

func worker(id int) {
	defer wg.Done()

	r := rand.New(rand.NewSource(time.Now().UnixNano()))

	for !done {
		mu.Lock()

		idx := r.Intn(arrayLength)
		action := ""
		if r.Intn(2) == 0 {
			arrays[id][idx]++
			action = "increased"
		} else {
			arrays[id][idx]--
			action = "decreased"
		}

		fmt.Printf("Worker %d %s element at index %d: %v\n", id, action, idx, arrays[id])

		sums := [3]int{0, 0, 0}
		for i, arr := range arrays {
			for _, v := range arr {
				sums[i] += v
			}
		}

		if sums[0] == sums[1] && sums[1] == sums[2] {
			equal = sums[0]
			done = true
			fmt.Println("Sums are equal:", sums)
		}

		mu.Unlock()
		time.Sleep(10 * time.Millisecond)
	}
}

func main() {
	wg.Add(3)
	for i := 0; i < 3; i++ {
		go worker(i)
	}
	wg.Wait()

	// Print results
	for _, arr := range arrays {
		fmt.Println(arr)
	}
	fmt.Printf("Equal sum: %d\n", equal)
}
