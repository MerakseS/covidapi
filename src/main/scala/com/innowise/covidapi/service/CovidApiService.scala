package com.innowise.covidapi.service

import cats.effect.Concurrent
import cats.implicits.*
import com.innowise.covidapi.dto.MinMaxCasesRequest
import com.innowise.covidapi.model.{Country, CovidCases}
import com.innowise.covidapi.service.CovidApiService
import io.circe.{Decoder, Encoder}
import org.http4s
import org.http4s.*
import org.http4s.Method.*
import org.http4s.UriTemplate.{ParamElm, PathElm}
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits.*

trait CovidApiService[F[_]]:
  def getCountryList: F[List[Country]]

  def get(covidCasesRequest: MinMaxCasesRequest): F[List[CovidCases]]

object CovidApiService:
  def apply[F[_]](implicit ev: CovidApiService[F]): CovidApiService[F] = ev

  private final case class CovidApiError(e: Throwable) extends RuntimeException

  private val baseUrl = uri"https://api.covid19api.com"

  def impl[F[_] : Concurrent](client: Client[F]): CovidApiService[F] = new CovidApiService[F]:
    val dsl = new Http4sClientDsl[F] {}

    import dsl.*

    override def getCountryList: F[List[Country]] = {
      client.expect[List[Country]](baseUrl / "countries")
        .adaptError { case t =>
          t.printStackTrace()
          CovidApiError(t)
        }
    }

    override def get(covidCasesRequest: MinMaxCasesRequest): F[List[CovidCases]] = {
      val uri = baseUrl / "country" / covidCasesRequest.country /
        "status" / "confirmed" +?
        ("from", covidCasesRequest.from) +?
        ("to", covidCasesRequest.to)

      client.expect[List[CovidCases]](uri)
        .adaptError { case t =>
          t.printStackTrace()
          CovidApiError(t)
        }
    }
