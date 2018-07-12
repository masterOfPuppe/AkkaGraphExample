import akka.{Done, NotUsed}
import akka.stream.scaladsl.{Flow, GraphDSL, Partition, Sink}
import akka.stream.{FanOutShape2, FlowShape, SinkShape}

import scala.concurrent.Future

object CustomPartition {

  /*Filter discards all flows in left sink. The sink is passed and define outside.
  * The left side is passed throw. In the Partition is specify the function used to divide the flow and the number of branches.
  * After it's necessary to specify the output destination of each branch.*/
  def apply[String](sink : Sink[Any, Future[Done]]) = GraphDSL.create[FlowShape[String, scala.Predef.String]]() {
    implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val partition = builder.add(Partition[String](2, x => if(x.toString.length < 5) 0 else 1))
      val left = builder.add(Flow[String].map(x => s"DONE ${x}"))

      partition.out(0) ~> sink
      partition.out(1) ~> left.in

      // This is the output passed where is specify who is the input and the unique output (we are using FlowShape and not FanOutShape#)
      new FlowShape(partition.in, left.out)
  }
}