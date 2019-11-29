package simulations

import io.gatling.core.Predef.{atOnceUsers, feed, jsonPath, repeat, scenario, _}
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.{http, status, _}
import io.gatling.http.protocol.HttpProtocolBuilder

class CsvFeederToCustom extends Simulation {


  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")
    .proxy(Proxy("localhost", 8888))

  val idNumbers: Iterator[Int] = (1 to 10).iterator

  val customFeeder: Iterator[Map[String, Int]] = Iterator.continually(Map("gameId" -> idNumbers.next))

  def checkSpecificVideoGame: ChainBuilder = {
    repeat(10) {
      feed(customFeeder)
        .exec(http("Get Specific Game")
          .get("videogames/${gameId}")
          //          .check(jsonPath("$.name").is("${gameName}"))
          .check(status.is(200)))
    }
  }


  val scn: ScenarioBuilder = scenario("Csv feeder test")
    .exec(checkSpecificVideoGame)


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
