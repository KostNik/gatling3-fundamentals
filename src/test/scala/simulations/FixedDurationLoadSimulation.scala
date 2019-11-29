package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._


class FixedDurationLoadSimulation extends Simulation {

  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  def checkAllVideoGames: ChainBuilder = {
    exec(
      http("Get all video games")
        .get("videogames")
        .check(status.is(200))
    )
  }

  def getSpecificGame: ChainBuilder = {
    exec(
      http("Get specific game")
        .get("videogames/2")
        .check(status.is(200))
    )
  }

  val scn: ScenarioBuilder = scenario("Fixed duration load simulation")
    .forever() {
      exec(checkAllVideoGames)
        .pause(2)
        .exec(getSpecificGame)
        .pause(3)
        .exec(checkAllVideoGames)
    }

  setUp(
    scn.inject(
      nothingFor(5 seconds),
      atOnceUsers(10),
      rampUsers(50) during(30 seconds)
    ).protocols(httpConf.inferHtmlResources)
  )

}
