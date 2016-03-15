import com.typesafe.config.ConfigFactory
/**
 * Created by mourad.benabdelkerim on 2/22/16.
 */
import spray.json.DefaultJsonProtocol._
import spray.json._
object Application {
  def main(args:Array[String]): Unit ={
    // Get configuration from 'resources/application.conf'
    val config = ConfigFactory.load()
    val gh_projectName = config.getString("github.projectName");
    val consumerKey = config.getString("twitter.consumerKey")
    val consumer_secret = config.getString("twitter.consumer_secret")
    val tweet_language = config.getString("twitter.tweet_language")
    val max_nb_tweet = config.getString("twitter.max_number_tweets").toInt

    // Get list of project from github
    val projectName:List[String] = GithubProjectFinder.getContent(gh_projectName)
    if(projectName != null){
      println("Total result = "+ projectName.length)
      val tweetFinder = new TweetFinder(consumerKey, consumer_secret, max_nb_tweet, tweet_language)
      var result:Map[String, Seq[String]] = Map[String, Seq[String]]()
      projectName.foreach(
        it=>{
              val res:Seq[String] = tweetFinder.get(it).toSeq
              if(res == null){
                return
              }
              result = result ++ Map(it -> res)
            }
      )
      // print out: project_name and its tweets
      println(result.toJson.prettyPrint)
    }
    else{
      println("There is no project in github with the name : " + gh_projectName)
    }
  }
}
