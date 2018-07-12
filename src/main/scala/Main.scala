import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Framing, GraphDSL, Partition, RunnableGraph, Sink, Source, StreamConverters}
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.util.ByteString
import cats.data._

object Main extends App {
  implicit val actorSystem = ActorSystem("validationStageDemo")
  implicit val materializer = ActorMaterializer()

  /*Create a graph that read a file content like a stream,
  discards lines with length more then 5, while prints the others lines*/
  RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    // read content file like a stream, parsing with frame length of 1024*1024
    val data = this.getClass.getResourceAsStream("/file_to_process.txt")
    val in = StreamConverters.fromInputStream(() => data)
      .via(Framing.delimiter(ByteString("\n"), 1024 * 1024, true))
      .map(_.utf8String)

    // create a sink for good lines and print them
    val out = Sink.foreach(println)

    // create a sink to discard bad lines (length less of 5)
    val sinkDiscard = Sink.foreach[Any](x => println(s"DISCARD FLOW ${x}"))

    /* create a new component like a graph and add this to the courrent graph using the functionality of .add.
    The use of .add is necessary when it's necessary add another graph to the current one
    or when you want specify the input and the output of your component*/
    val partVal = builder.add(CustomPartition[String](sinkDiscard))

    // Define the flow for current graph
    in ~> partVal ~> out

    ClosedShape
  }).run()
}