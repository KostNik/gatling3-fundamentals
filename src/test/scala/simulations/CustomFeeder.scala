package simulations

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.gatling.core.Predef.{atOnceUsers, feed, repeat, scenario, _}
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.{http, status}
import io.gatling.http.check.status.HttpStatusCheckMaterializer
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.util.Random

class CustomFeeder extends Simulation {

  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")


  val idNumbers: Iterator[Int] = (11 to 20).iterator
  val rnd = new Random()
  val pattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val now: LocalDate = LocalDate.now

  def randomString(length: Int): String = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def randomDate(startDate: LocalDate, random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }

  val fields: Set[String] = Set("gameId", "name", "releaseDate", "reviewScore", "category", "rating")

  val customFeeder: Iterator[Map[String, Any]] = Iterator.continually(Map(
    "gameId" -> idNumbers.next,
    "name" -> s"Game-${randomString(5)}",
    "releaseDate" -> randomDate(now, rnd),
    "reviewScore" -> rnd.nextInt(100),
    "category" -> s"Category-${randomString(6)}",
    "rating" -> s"Rating-${randomString(4)}"
  ))

  def postNewGame(): ChainBuilder = {
    repeat(5) {
      // now call the feeder here
      feed(customFeeder)
        .exec(http("Post New Game")
          .post("videogames")
          .body(ElFileBody("bodies/NewGameTemplate.json")).asJson //template file goes in resources/bodies
          .check(status.is(200).build(HttpStatusCheckMaterializer))
        )
        .pause(1)
    }
  }


  val scn: ScenarioBuilder = scenario("Post new games")
    .exec(postNewGame())


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)


}
