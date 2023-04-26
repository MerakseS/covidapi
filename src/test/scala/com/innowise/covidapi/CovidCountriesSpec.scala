package com.innowise.covidapi

import cats.effect.{Concurrent, IO, Resource}
import com.innowise.covidapi.model.{Country, MinMaxCases}
import com.innowise.covidapi.route.CovidApiRoutes
import com.innowise.covidapi.service.CovidApiService
import io.circe.{Decoder, Encoder}
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.client.JavaNetClientBuilder
import org.http4s.implicits.*

class CovidCountriesSpec extends CatsEffectSuite {
  val client = JavaNetClientBuilder[IO].create
  val service = CovidApiService.impl[IO](client)
  val routes = CovidApiRoutes.covidCasesRoutes(service)

  given Decoder[Country] = Decoder.derived[Country]

  given[F[_] : Concurrent]: EntityDecoder[F, Country] = jsonOf

  given Decoder[List[Country]] = Decoder.decodeList[Country]

  given[F[_] : Concurrent]: EntityDecoder[F, List[Country]] = jsonOf

  test("Countries amount") {
    val response = retCountryList()

    assertIO(response.map(_.status), Status.Ok)
    assertIO(response.flatMap(_.as[List[Country]]).map(_.length), 248)
  }

  private def retCountryList(): IO[Response[IO]] = {
    val getMinMaxCasesRequest = Request[IO](Method.GET, uri"/covid/countries")
    routes.orNotFound(getMinMaxCasesRequest)
  }
}
