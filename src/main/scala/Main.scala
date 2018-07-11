import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, GraphDSL, Partition, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}
import cats.data._

object Main extends App {
  implicit val actorSystem = ActorSystem("validationStageDemo")
  implicit val materializer = ActorMaterializer()

  RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val in = Source(1 to 10)
    val out = Sink.foreach(println)
    val discardSink = Sink.ignore

    val partVal = builder.add(CustomPartition[Int])

    in ~> partVal.in
    partVal.out0 ~> out
    partVal.out1 ~> discardSink

    ClosedShape
  }).run()
}