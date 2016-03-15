/**
 * Created by mourad.benabdelkerim on 2/22/16.
 */
import java.util.Map

import twitter4j._
import twitter4j.auth.OAuth2Token
import twitter4j.conf.ConfigurationBuilder

import scala.util.control.Breaks._
/*
 *
 */
object TweetFinder  {
  private var CONSUMER_KEY: String = null
  private var CONSUMER_SECRET: String = null
  private var nb_of_tweets: Int = 0
  //private val MAX_QUERIES: Int = 1

  /** Replace newlines and tabs in text with escaped versions to making printing cleaner
    * @param text The text of a tweet, sometimes with embedded newlines and tabs
    * @return The text passed in, but with the newlines and tabs replaced
    */
  def cleanText(text: String): String = {
    var ntext = text.replace("\n", "\\n")
    ntext = text.replace("\t", "\\t")
    return ntext
  }

  /** Retrieve the "bearer" token from Twitter in order to make application-authenticated calls.
    * This is the first step in doing application authentication, as described in Twitter's documentation at
    * https://dev.twitter.com/docs/auth/application-only-auth
    * Note that if there's an error in this process, we just print a message and quit. That's a pretty
    * dramatic side effect, and a better implementation would pass an error back up the line...
    * @return The oAuth2 bearer token
    */
  def getOAuth2Token: OAuth2Token = {
    var token: OAuth2Token = null
    var cb: ConfigurationBuilder = null
    cb = new ConfigurationBuilder
    cb.setApplicationOnlyAuthEnabled(true)
    cb.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET)
    try {
      token = new TwitterFactory(cb.build).getInstance.getOAuth2Token
    }
    catch {
      case e: Exception => {
        System.out.println("Could not get OAuth2 token: Check again your Twitter(consumer Key, consumer secret) in `resources/application.conf file`.")
        //e.printStackTrace
        System.exit(0)
      }
    }
    return token
  }

  /** * Get a fully application-authenticated Twitter object useful for making subsequent calls.
    * * @return Twitter4J Twitter object that's ready for API calls
    */
  def getTwitter(consumer_key:String,
                 consumer_secret:String,
                 tweet_max:Int): Twitter = {
    this.CONSUMER_KEY = consumer_key
    this.CONSUMER_SECRET = consumer_secret
    this.nb_of_tweets = tweet_max
    var token: OAuth2Token = null
    token = getOAuth2Token
    val cb: ConfigurationBuilder = new ConfigurationBuilder
    cb.setApplicationOnlyAuthEnabled(true)
    cb.setOAuthConsumerKey(this.CONSUMER_KEY)
    cb.setOAuthConsumerSecret(this.CONSUMER_SECRET)
    cb.setOAuth2TokenType(token.getTokenType)
    cb.setOAuth2AccessToken(token.getAccessToken)
    return new TwitterFactory(cb.build).getInstance
  }
}
class TweetFinder(consumer_key:String,
                  consumer_secret:String,
                  tweet_max:Int,
                  tweet_language:String)
{
  private var search_term: String = null
  private var totalTweets: Int = 0
  private var maxID: Long = -1
  private val CONSUMER_KEY: String = consumer_key
  private val CONSUMER_SECRET: String = consumer_secret
  private val twitter: Twitter = TweetFinder .getTwitter(CONSUMER_KEY, CONSUMER_SECRET, tweet_max)

  private val tweetType = "recent"
  private val tweet_lang = tweet_language


  def get(item: String):List[String] = {
    var result:List[String] = List()
    this.totalTweets = 0
    this.search_term = item
    this.maxID = -1
    try {
      val rateLimitStatus: Map[String, RateLimitStatus] = this.twitter.getRateLimitStatus("search")
      var searchTweetsRateLimit: RateLimitStatus = rateLimitStatus.get("/search/tweets")
      var queryNumber: Int = 0
      breakable {
            if (searchTweetsRateLimit.getRemaining == 0) {
              Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset + 2) * 1000l)
            }
            val q: Query = new Query(search_term)
            q.setCount(TweetFinder.nb_of_tweets)
            q.resultType(this.tweetType)
            q.setLang(this.tweet_lang)
            if (this.maxID != -1) {
              q.setMaxId(this.maxID - 1)
            }
            val r: QueryResult = this.twitter.search(q)
            if (r.getTweets.size == 0) {
              break
            }
            import scala.collection.JavaConversions._
            for (s <- r.getTweets) {
              this.totalTweets += 1
              if (this.maxID == -1 || s.getId < this.maxID) {
                this.maxID = s.getId
              }
              result = result ++ List(TweetFinder.cleanText(s.getText))
            }
            searchTweetsRateLimit = r.getRateLimitStatus
      }
      }
    catch {
      case e: Any => {
        println("The application's rate limit having been reached!!")
        return null
      }
    }
    return result
  }
}