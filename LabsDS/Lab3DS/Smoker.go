package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

var table = make(chan []string, 1)
var doneSmoking = make(chan bool)

func smoker(id int, ownItem string, wg *sync.WaitGroup) {
	defer wg.Done()

	for {
		select {
		case items := <-table:
			if !contains(items, ownItem) {
				fmt.Printf("Smoker %d with %s is smoking...\n", id, ownItem)
				time.Sleep(time.Second)
				doneSmoking <- true
			} else {
				table <- items
			}
		default:
		}
	}
}

func mediator(wg *sync.WaitGroup) {
	defer wg.Done()

	items := []string{"tobacco", "paper", "matches"}

	for {
		rand.Seed(time.Now().UnixNano())
		i, j := rand.Intn(3), rand.Intn(3)
		for i == j {
			j = rand.Intn(3)
		}

		fmt.Printf("Mediator puts %s and %s on the table\n", items[i], items[j])
		table <- []string{items[i], items[j]}

		<-doneSmoking
	}
}

func contains(slice []string, item string) bool {
	for _, a := range slice {
		if a == item {
			return true
		}
	}
	return false
}

func main() {
	var wg sync.WaitGroup

	wg.Add(4)
	go smoker(1, "tobacco", &wg)
	go smoker(2, "paper", &wg)
	go smoker(3, "matches", &wg)
	go mediator(&wg)

	wg.Wait()
}
