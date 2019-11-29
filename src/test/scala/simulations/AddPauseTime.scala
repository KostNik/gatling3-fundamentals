package simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class AddPauseTime extends Simulation {

  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  val scn: ScenarioBuilder = scenario("Video Game Db - 3 calls")

    .exec(http("Get all video games - 1st call")
      .get("videogames"))
    .pause(5)

    .exec(http("Get specific game")
      .get("videogames/1"))
    .pause(1, 20)

    .exec(http("Get all video games - 2st call")
      .get("videogames"))
    .pause(3000.milliseconds)


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
