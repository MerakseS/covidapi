package com.innowise.covidapi

import cats.effect.{IO, Resource}
import com.innowise.covidapi.model.MinMaxCases
import com.innowise.covidapi.route.CovidApiRoutes
import com.innowise.covidapi.service.CovidApiService
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.client.JavaNetClientBuilder
import org.http4s.implicits.*

import java.time.ZonedDateTime

class CovidCasesSpec extends CatsEffectSuite:
  val client = JavaNetClientBuilder[IO].create
  val service = CovidApiService.impl[IO](client)
  val routes = CovidApiRoutes.covidCasesRoutes(service)

  test("Minimal and maximum cases") {
    val expected = MinMaxCases("Belarus",
      47, ZonedDateTime.parse("2020-04-03T00:00:00Z"),
      141, ZonedDateTime.parse("2020-04-02T00:00:00Z"))

    val response = retMinMaxCases("belarus", "2020-04-01", "2020-04-05")

    assertIO(response.map(_.status), Status.Ok)
    assertIO(response.flatMap(_.as[MinMaxCases]), expected)
  }

  test("MinMaxCases without provinces") {
    val expected = MinMaxCases("United Kingdom",
      3594, ZonedDateTime.parse("2020-04-05T00:00:00Z"),
      4915, ZonedDateTime.parse("2020-04-03T00:00:00Z"))

    val response = retMinMaxCases("uk", "2020-04-01", "2020-04-05")

    assertIO(response.map(_.status), Status.Ok)
    assertIO(response.flatMap(_.as[MinMaxCases]), expected)
  }

  private def retMinMaxCases(country: String, from: String, to: String): IO[Response[IO]] = {
    val uri = uri"/covid" / "country" / country +? ("from", from) +? ("to", to)
    val getMinMaxCasesRequest = Request[IO](Method.GET, uri)

    routes.orNotFound(getMinMaxCasesRequest)
  }
