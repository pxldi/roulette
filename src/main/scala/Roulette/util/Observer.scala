package Roulette
package util

trait Observer:
  def update(idx: Int, e: Event): Unit

trait Observable:
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers = subscribers :+ s
  def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)
  def notifyObservers(idx: Int, e: Event) = subscribers.foreach(o => o.update(idx, e))

enum Event {
  case QUIT, PLAY
}