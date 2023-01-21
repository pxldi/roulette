package Roulette
package util

trait Observer:
  def update(e: Event): Unit

trait Observable:
  private var subscribers: Vector[Observer] = Vector()
  def add(s: Observer): Unit = subscribers = subscribers :+ s
  def remove(s: Observer): Unit = subscribers = subscribers.filterNot(o => o == s)
  def notifyObservers(e: Event): Unit = subscribers.foreach(o => o.update(e))

enum Event {
  case UPDATE, P1WIN, P2WIN, DRAW, QUIT
}
