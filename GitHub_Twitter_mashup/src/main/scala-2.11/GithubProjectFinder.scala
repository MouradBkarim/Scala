/**
 * Created by mourad.benabdelkerim on 2/22/16.
 */
import spray.json._
import scalaj.http.HttpResponse

object GithubProjectFinder {
  /** *
    * It make a http request to github and get a list of project's name that contains param.
    * @param gh_projectName
    * @return list of projects that name contains param
    */
  def getContent(gh_projectName:String): List[String] ={
    import scalaj.http.Http
    var result:List[String] = null
    try{
    val response: HttpResponse[String] = Http("https://api.github.com/search/repositories").param("q", gh_projectName).asString
    val data = response.body.toString
    // Convert data to String and next to json. Extract 'items' list
    // Parse item's list and extract the 'name' field.
    result = data.parseJson
                        .asJsObject.getFields("items")(0).asInstanceOf[JsArray]
                        .elements
                        .map(
                          item =>{
                            item.asJsObject.getFields("name")(0).toString.replaceAll("\"", "")
                          }
                        ).toList
    }
    catch{
      case e:Any=>{
        println("Can not connect to : https://api.github.com/search/repositories")
      }
    }
    result
  }
}
