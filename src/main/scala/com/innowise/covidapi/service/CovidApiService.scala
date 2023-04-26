package com.innowise.covidapi.service

import cats.effect.Concurrent
import cats.implicits.*
import com.innowise.covidapi.model.{Country, CovidCases, MinMaxCases}
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

import java.time.ZonedDateTime

trait CovidApiService[F[_]]:
  def getCountryList: F[List[Country]]

  def getMinMaxCases(country: String, from: String, to: String): F[MinMaxCases]

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

    override def getMinMaxCases(country: String, from: String, to: String): F[MinMaxCases] = {
      for {
        covidCasesList <- getCovidCasesList(country, from, to)
        covidCasesListWithoutProvinces = covidCasesList.filter(_.province.isEmpty)
        minMaxCasesResponse = calculateMinMaxCases(covidCasesListWithoutProvinces)
      } yield minMaxCasesResponse
    }

    private def getCovidCasesList(country: String, from: String, to: String): F[List[CovidCases]] = {
      val uri = baseUrl / "country" / country / "status" / "confirmed" +?
        ("from", from) +? ("to", to)

      (for {
        covidCasesList <- client.expect[List[CovidCases]](uri)
        covidCasesListWithoutProvinces = covidCasesList.filter(_.province.isEmpty)
      } yield covidCasesListWithoutProvinces
        ).adaptError { case t =>
        t.printStackTrace()
        CovidApiError(t)
      }
    }

    private def calculateMinMaxCases(covidCasesList: List[CovidCases]): MinMaxCases = {
      var minNewCases = Int.MaxValue
      var minNewCasesDate: ZonedDateTime = null

      var maxNewCases = Int.MinValue
      var maxNewCasesDate: ZonedDateTime = null

      for (i <- 1 until covidCasesList.length) {
        val newCases = covidCasesList(i).cases - covidCasesList(i - 1).cases
        if (newCases < minNewCases) {
          minNewCases = newCases
          minNewCasesDate = covidCasesList(i).date
        }

        if (newCases > maxNewCases) {
          maxNewCases = newCases
          maxNewCasesDate = covidCasesList(i).date
        }
      }

      MinMaxCases(covidCasesList.head.country, minNewCases, minNewCasesDate,
        maxNewCases, maxNewCasesDate)
    }
