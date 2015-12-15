package github

import java.util.Calendar

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import org.elasticsearch.spark._

object GhEvent {
  implicit lazy val formats = DefaultFormats

  def main(args: Array[String]): Unit = {

    if (args.length < 3) {
      System.err.println("Usage: GhEvent <github Data Directory> <ES ip> <ES port>")
      System.exit(1)
    }
    val githubDataDirectory = args(0)

    // Spark configuration
    val defConf = new SparkConf(true)
    val sparkConf = defConf.setAppName("GhEvent").
      setMaster(defConf.get("spark.master",  "local[*]"))

    // ES configuration
    sparkConf.set("es.index.auto.create", "true")
    sparkConf.set("es.nodes",args(1))
    sparkConf.set("es.port",args(2))

    val sc = new SparkContext(sparkConf)
    
    // Dataset processing
    val data: RDD[String] = sc.textFile(githubDataDirectory + "/*").flatMap(_.split("\n"))

    val processedData = data.map {
      line =>
        val evType = (parse(line) \ "type").extract[String]
        val evTime = (parse(line) \ "created_at").extract[String]
        val evLanguage = if (evType == "PullRequestEvent") {
          (parse(line) \ "payload" \ "pull_request" \ "head" \ "repo" \ "language").extractOrElse[String]("")
        } else if (evType == "ForkEvent") {
          (parse(line) \ "payload" \ "forkee" \ "language").extractOrElse[String]("")
        } else if (evType == "IssuesEvent" ||
          evType == "CreateEvent" ||
          evType == "DeleteEvent" ||
          evType == "PushEvent" ||
          evType == "ReleaseEvent" ||
          evType == "WatchEvent") {
          (parse(line) \ "payload" \ "repository" \ "language").extractOrElse[String]("")
        } else ""

        Map("timestamp" -> Calendar.getInstance().getTime(),
          "source" -> "gh-event-info",
          "event_type" -> evType,
          "created_at" -> evTime,
          "language" -> evLanguage)
    }

    // Save to ES
    processedData.saveToEs("github/evinfo")

    sc.stop()
  }
}
