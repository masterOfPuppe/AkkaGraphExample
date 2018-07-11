import akka.NotUsed
import akka.stream.scaladsl.{Flow, GraphDSL, Partition}
import akka.stream.FanOutShape2

object CustomPartition {

  def apply[Int]() = GraphDSL.create[FanOutShape2[scala.Int, scala.Int, scala.Int]](){
    implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    val left = builder.add(Flow[scala.Int])
    val right = builder.add(Flow[scala.Int])
    val partition = builder.add(Partition[scala.Int](2, x => if(x < 5) 0 else 1))

    partition.out(0) ~> left.in
    partition.out(1) ~> right.in

    new FanOutShape2(partition.in, left.out, right.out)
  }
}