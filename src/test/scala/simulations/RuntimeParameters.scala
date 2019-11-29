package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._


class RuntimeParameters extends Simulation {

  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  def checkAllVideoGames: ChainBuilder = {
    exec(
      http("Get all video games")
        .get("videogames")
        .check(status.is(200))
    )
  }


  val scn: ScenarioBuilder = scenario("Get all video games")
    .forever {
      exec(checkAllVideoGames)
    }


  setUp(
    scn.inject(
      nothingFor(5 seconds),
      rampUsers(1) during (1 second)
    )
  ).protocols(httpConf)
    .maxDuration(30 seconds)

}
