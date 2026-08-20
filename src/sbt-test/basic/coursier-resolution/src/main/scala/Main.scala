import cats.data.NonEmptyList
import cats.free.Free

object Main {
  val values = NonEmptyList.of(1, 2, 3)
  type Program[A] = Free[Option, A]
}
