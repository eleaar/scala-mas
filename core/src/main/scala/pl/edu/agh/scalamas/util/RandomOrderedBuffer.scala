/*
 * Copyright (c) 2013 Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pl.edu.agh.scalamas.util

import org.apache.commons.math3.random.RandomDataGenerator

import scala.collection.mutable.ArrayBuffer

class RandomOrderedBuffer[T](implicit ordering: Ordering[T]) {

  // Heap algo copied and adapted from http://www.sanfoundry.com/java-program-implement-binary-heap/
  private val heap = ArrayBuffer[T]()

  def add(element: T): Unit = {
    heap += element
    heapifyUp(size - 1)
  }

  def removeRandom()(implicit random: RandomDataGenerator): Option[T] = {
    if (heap.isEmpty) {
      None
    } else {
      Some(deleteAt(random.nextInt(0, size - 1)))
    }
  }

  def removeMax(): Option[T] = {
    if (heap.isEmpty) {
      None
    } else {
      Some(deleteAt(0))
    }
  }

  private def deleteAt(ind: Int): T = {
    val keyItem = heap(ind)
    heap(ind) = heap(size - 1)
    heap.remove(size - 1)
    if(ind < size) {
      heapifyDown(ind)
    }
    keyItem
  }

  private def heapifyUp(index: Int): Unit = {
    val targetElement = heap(index)

    var currentIndex = index
    var parentIndex = parent(currentIndex)
    var parentElement = heap(parentIndex)

    while (currentIndex > 0 && ordering.gt(targetElement, parentElement)) {
      heap(currentIndex) = parentElement

      currentIndex = parentIndex
      parentIndex = parent(currentIndex)
      parentElement = heap(parentIndex)
    }

    heap(currentIndex) = targetElement
  }

  // This implementation is really ugly, but I wanted to avoid the allocation overhead of using options to model the absence of children...
  private def heapifyDown(index: Int): Unit = {
    val targetElement = heap(index)

    def maxChild(ind: Int): Int = {
      val firstIndex = firstChild(ind)
      val secondIndex = secondChild(ind)

      if (firstIndex < size) {
        if(secondIndex < size) {
          if(ordering.gt(heap(firstIndex), heap(secondIndex))) {
            firstIndex
          } else {
            secondIndex
          }
        } else {
          firstIndex
        }
      } else {
        -1
      }
    }

    var currentIndex = index
    // at this point the childIndex may point out of bounds, but me make a check below before accessing the location
    var childIndex = maxChild(currentIndex)

    while (
      hasChildren(currentIndex) &&
        ordering.gt(heap(childIndex), targetElement)
    ) {
      heap(currentIndex) = heap(childIndex)

      currentIndex = childIndex
      childIndex = maxChild(currentIndex)
    }

    heap(currentIndex) = targetElement
  }

  @inline
  def size: Int = heap.size

  def isEmpty: Boolean = heap.isEmpty

  @inline
  private def parent(i: Int) = (i - 1) / 2

  @inline
  private def firstChild(i: Int) = 2 * i + 1

  @inline
  private def secondChild(i: Int) = 2 * i + 2

  @inline private def hasChildren(i: Int) = firstChild(i) < size

}
