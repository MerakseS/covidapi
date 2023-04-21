package com.innowise.covidapi.model

import cats.effect.Concurrent
import cats.implicits.*
import com.innowise.covidapi.model.CovidCases
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*

import java.time.ZonedDateTime

case class CovidCases(country: String, province: String, cases: Int, date: ZonedDateTime)

object CovidCases:
  given Decoder[CovidCases] = Decoder.instance { h =>
    for {
      country <- h.get[String]("Country")
      province <- h.get[String]("Province")
      cases <- h.get[Int]("Cases")
      date <- h.get[ZonedDateTime]("Date")
    } yield CovidCases(country, province, cases, date)
  }

  given[F[_] : Concurrent]: EntityDecoder[F, CovidCases] = jsonOf

  given Encoder[CovidCases] = Encoder.AsObject.derived[CovidCases]

  given[F[_]]: EntityEncoder[F, CovidCases] = jsonEncoderOf

  given Decoder[List[CovidCases]] = Decoder.decodeList[CovidCases]

  given[F[_] : Concurrent]: EntityDecoder[F, List[CovidCases]] = jsonOf

  given Encoder[List[CovidCases]] = Encoder.encodeList[CovidCases]

  given[F[_]]: EntityEncoder[F, List[CovidCases]] = jsonEncoderOf
