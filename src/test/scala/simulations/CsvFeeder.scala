package simulations


import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class CsvFeeder extends Simulation {


  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  val csvFeeder: BatchableFeederBuilder[String]#F = csv("data/gameCsvFile.csv").circular

  def checkSpecificVideoGame: ChainBuilder = {
    repeat(10) {
      feed(csvFeeder)
        .exec(http("Get Specific Game")
          .get("videogames/${gameId}")
          .check(jsonPath("$.name").is("${gameName}"))
          .check(status.is(200)))
    }
  }


  val scn: ScenarioBuilder = scenario("Csv feeder test")
    .exec(checkSpecificVideoGame)


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
