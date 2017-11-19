/*
 * o                        o     o   o         o
 * |             o          |     |\ /|         | /
 * |    o-o o--o    o-o  oo |     | O |  oo o-o OO   o-o o   o
 * |    | | |  | | |    | | |     |   | | | |   | \  | |  \ /
 * O---oo-o o--O |  o-o o-o-o     o   o o-o-o   o  o o-o   o
 *             |
 *          o--o
 * o--o              o               o--o       o    o
 * |   |             |               |    o     |    |
 * O-Oo   oo o-o   o-O o-o o-O-o     O-o    o-o |  o-O o-o
 * |  \  | | |  | |  | | | | | |     |    | |-' | |  |  \
 * o   o o-o-o  o  o-o o-o o o o     o    | o-o o  o-o o-o
 *
 * Logical Markov Random Fields LoMRF (LoMRF).
 */

package lomrf.util.collection

import scala.reflect.ClassTag
//import scalaxy.streams.optimize

trait IndexPartitioned[T] extends (Int => T) {

  /**
   * Gives the corresponding object that is associated to the specified index value,
   * possibly by using an hash partitioning function.
   *
   * @param idx the index value
   *
   * @return the corresponding object
   */
  def apply(idx: Int): T

  /**
   * @return the number of partitions
   */
  def size: Int

  /**
   * @return an iterable collection that contains all partitions
   */
  def partitions: Iterable[T]

  /**
   * Direct access to corresponding object at the specified partition index.
   *
   * @param partitionIndex partition index
   * @return corresponding object
   */
  def partition(partitionIndex: Int): T

}

object IndexPartitioned {

  object hash {
    def apply[T](data: Array[T]): IndexPartitioned[T] = new IndexPartitioned[T] {

      private val partitioner = Partitioner.hash[Int](data.length)

      override def apply(idx: Int) = data(partitioner(idx))

      override def partitions = data.toIterable

      override def size = data.length

      override def partition(partitionIndex: Int) = data(partitionIndex)
    }

    def apply[T: ClassTag](size: Int, initializer:(Int => T)): IndexPartitioned[T] = {
      val data = new Array[T](size)

      //optimize{
      for(i <- 0 until size) data(i) = initializer(i)
      //}

      apply(data)
    }

    def apply[T: ClassTag](size: Int): IndexPartitioned[T] = apply( new Array[T](size))
  }

}
