package main

import (
	"fmt"
	"sync"
	"time"
)

type Route struct {
	price int
}

type CityGraph struct {
	sync.RWMutex
	graph map[string]map[string]*Route
}

func (g *CityGraph) AddCity(cityName string) {
	g.Lock()
	defer g.Unlock()
	if _, ok := g.graph[cityName]; !ok {
		g.graph[cityName] = make(map[string]*Route)
	}
}

func (g *CityGraph) RemoveCity(cityName string) {
	g.Lock()
	defer g.Unlock()
	delete(g.graph, cityName)
	for _, routes := range g.graph {
		delete(routes, cityName)
	}
}

func (g *CityGraph) AddRoute(cityA, cityB string, price int) {
	g.Lock()
	defer g.Unlock()
	if _, ok := g.graph[cityA]; ok {
		g.graph[cityA][cityB] = &Route{price: price}
	}
	if _, ok := g.graph[cityB]; ok {
		g.graph[cityB][cityA] = &Route{price: price}
	}
}

func (g *CityGraph) RemoveRoute(cityA, cityB string) {
	g.Lock()
	defer g.Unlock()
	delete(g.graph[cityA], cityB)
	delete(g.graph[cityB], cityA)
}

func (g *CityGraph) ChangePrice(cityA, cityB string, newPrice int) {
	g.Lock()
	defer g.Unlock()
	if route, ok := g.graph[cityA][cityB]; ok {
		route.price = newPrice
	}
	if route, ok := g.graph[cityB][cityA]; ok {
		route.price = newPrice
	}
}

func (g *CityGraph) PathExists(cityA, cityB string) (bool, int) {
	g.RLock()
	defer g.RUnlock()
	if route, ok := g.graph[cityA][cityB]; ok {
		return true, route.price
	}
	return false, 0
}

func main() {
	graph := &CityGraph{
		graph: make(map[string]map[string]*Route),
	}

	go func() {
		for {
			time.Sleep(5 * time.Second)
			graph.ChangePrice("CityA", "CityB", 100)
			fmt.Println("Price changed!")
		}
	}()

	go func() {
		for {
			time.Sleep(7 * time.Second)
			graph.AddRoute("CityC", "CityD", 200)
			fmt.Println("Route added!")
		}
	}()

	go func() {
		for {
			time.Sleep(10 * time.Second)
			graph.RemoveCity("CityE")
			fmt.Println("City removed!")
		}
	}()

	go func() {
		for {
			time.Sleep(3 * time.Second)
			exists, price := graph.PathExists("CityA", "CityB")
			if exists {
				fmt.Printf("Path exists with price: %d\n", price)
			} else {
				fmt.Println("Path doesn't exist!")
			}
		}
	}()

	time.Sleep(60 * time.Second)
}
