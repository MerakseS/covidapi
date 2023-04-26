package com.innowise.covidapi.route

import cats.effect.{Concurrent, Sync}
import cats.implicits.*
import com.innowise.covidapi.model.CovidCases
import com.innowise.covidapi.service.CovidApiService
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher

object CovidApiRoutes:

  private object FromDateQueryParamMatcher extends QueryParamDecoderMatcher[String]("from")

  private object ToDateQueryParamMatcher extends QueryParamDecoderMatcher[String]("to")

  def covidCasesRoutes[F[_] : Sync](covidApiService: CovidApiService[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "covid" / "countries" =>
        for {
          countryList <- covidApiService.getCountryList
          resp <- Ok(countryList)
        } yield resp
      case GET -> Root / "covid" / "country" / country :?
        FromDateQueryParamMatcher(from) +& ToDateQueryParamMatcher(to) =>
        for {
          covidCases <- covidApiService.getMinMaxCases(country, from, to)
          resp <- Ok(covidCases)
        } yield resp
    }
