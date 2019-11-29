package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._


class RampUsersLoadSimulation extends Simulation {

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

  val scn: ScenarioBuilder = scenario("Basic Load Simulation")
    .exec(checkAllVideoGames)
    .pause(2)
    .exec(getSpecificGame)
    .pause(3)
    .exec(checkAllVideoGames)

  setUp(
    scn.inject(
      nothingFor(5 seconds),
      //      constantUsersPerSec(10) during(10 seconds)
      rampUsersPerSec(1) to 5 during (20 seconds)
    ).protocols(httpConf.inferHtmlResources)
  )

}
