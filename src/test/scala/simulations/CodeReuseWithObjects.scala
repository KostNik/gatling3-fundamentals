package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class CodeReuseWithObjects extends Simulation {

  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

   def checkAllVideoGames: ChainBuilder = {
    exec(http("Get all video games -1st call")
      .get("videogames")
      .check(status.is(200)))
  }

  def checkSpecificVideoGame: ChainBuilder = {
    exec(http("Get specific game")
      .get("videogames/1")
      .check(status.in(200 to 210)))
  }

  val scn: ScenarioBuilder = scenario("Code reuse")
    .exec(checkAllVideoGames)
    .pause(5)
    .exec(checkSpecificVideoGame)
    .pause(5)
    .exec(checkAllVideoGames)


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)


}
