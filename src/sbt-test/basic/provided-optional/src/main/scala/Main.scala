import cats.data.NonEmptyList
import cats.kernel.Eq

object Main:
  val values = NonEmptyList.of(1, 2, 3)
  val eq: Eq[Int] = Eq[Int]
