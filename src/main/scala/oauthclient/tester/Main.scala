package oauthclient.tester

import scala.App
import scala.Console
import com.ning.http.client._
import java.util.concurrent.Future
import java.util.Date
import javax.crypto.Mac
import javax.crypto.spec._

object Signer {
  private def MAC_NAME:String = "HmacSHA1";
    
  def percentEncode(s:String)=s.replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
  def apply(method: String, secret: String, tokenSecret: String) = {
    method match {
      case "PLAINTEXT" => secret + "%26" + tokenSecret
      case "HMAC-SHA1" => {

        
        val keyBytes=(percentEncode(secret)+"&"+percentEncode(tokenSecret)).getBytes("UTF-8")
        val key=new SecretKeySpec(keyBytes,MAC_NAME)
        val mac=Mac.getInstance(MAC_NAME)
        mac.init(key)
        mac.doFinal()
      }
    }
  }
}

class OAuthURLBuilder(_host: String, _consumerKey: String, _consumerSecret: String, _signatureMethod: String, _token: String, _tokenSecret: String, _nonce: String) {
  
  private def ap(from: String, param: String): String = {
    // if (param.last.toString == "=")
    //   from
    //   else
    if (from.exists(_.toString == "?"))
      from + "&" + param
    else
      from + "?" + param
  }

  object p {
    def consumerKey = "oauth_consumer_key=" + _consumerKey
    def signatureMethod = "oauth_signature_method=" + _signatureMethod
    //def signature = "oauth_signature=" + Signer(_signatureMethod, _consumerSecret, _tokenSecret)
    // def token=""+
    def timeStamp = "oauth_timestamp=" + ((new Date()).getTime() / 1000)
    def nonce = "oauth_nonce=" + _nonce
  }
      
  def getUrl: String = {
    var t = ap(_host, p.consumerKey)
    t = ap(t, p.signatureMethod)
    t = ap(t, p.signature)
    // t=ap(t,p.token)
    t = ap(t, p.timeStamp)
    t = ap(t, p.nonce)
    t
    //http://127.0.0.1:8080/oauth/request_token?oauth_consumer_key=myKey&oauth_timestamp=1314846042&oauth_nonce=f&oauth_signature_method=PLAINTEXT&oauth_signature=mySecret&&oauth_token=283396785ad97fc9207040be69b2c40f&oauth_token_secret=bc972b3105b09f5ed3802d8a55e68f1
    }
  
}

object Main extends App {
  override def main(args: Array[String]) {
    args.foreach(arg => Console.println(arg))
    val client = new AsyncHttpClient()
    val curDate = (new Date).getTime
    // val url=(new OAuthURLBuilder("http://127.0.0.1:8080/oauth/request_token","myKey","mySecret","PLAINTEXT","","","")).getUrl
    var url = (new OAuthURLBuilder("https://www.google.com/accounts/OAuthGetRequestToken", "26291902919.apps.googleusercontent.com", "SDbvQF-IUQ84YznYNNj63lEv", "HMAC-SHA1", "", "", "")).getUrl
    url = url + "&scope=http://www.google.com/calendar/feeds"
    Console.println(url)
    // authorize
    // HTMLUnit in authorize
    val f = client.prepareGet(url).execute()
    val r = f.get
    Console.println(r.getResponseBody)
    return
  }
}

