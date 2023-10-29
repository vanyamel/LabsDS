package main

import (
	"fmt"
	"sync"
)

type Monk struct {
	Energy int
	Name   string
}

func Fight(a, b Monk) Monk {
	if a.Energy > b.Energy {
		return a
	}
	return b
}

func Tournament(monks []Monk, c chan Monk, wg *sync.WaitGroup) {
	defer wg.Done()

	if len(monks) == 1 {
		c <- monks[0]
		return
	}

	mid := len(monks) / 2

	ch1 := make(chan Monk)
	ch2 := make(chan Monk)

	var internalWg sync.WaitGroup

	internalWg.Add(2)
	go Tournament(monks[:mid], ch1, &internalWg)
	go Tournament(monks[mid:], ch2, &internalWg)

	internalWg.Wait()

	monk1 := <-ch1
	monk2 := <-ch2

	winner := Fight(monk1, monk2)

	c <- winner
}

func main() {
	monks := []Monk{
		{Energy: 10, Name: "A"},
		{Energy: 8, Name: "B"},
		{Energy: 7, Name: "C"},
		{Energy: 9, Name: "D"},
	}

	ch := make(chan Monk)
	var wg sync.WaitGroup
	wg.Add(1)
	go Tournament(monks, ch, &wg)

	wg.Wait()
	winner := <-ch
	fmt.Println("The winner is:", winner.Name)
}
