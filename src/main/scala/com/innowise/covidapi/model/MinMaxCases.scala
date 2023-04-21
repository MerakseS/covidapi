package com.innowise.covidapi.model

import cats.effect.Concurrent
import cats.implicits.*
import com.innowise.covidapi.model.CovidCases
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*

import java.time.ZonedDateTime

case class MinMaxCases(country: String, minCases: Int, minCasesDate: ZonedDateTime, 
                       maxCases: Int, maxCasesDate: ZonedDateTime)

object MinMaxCases:
  given Decoder[MinMaxCases] = Decoder.derived[MinMaxCases]

  given[F[_] : Concurrent]: EntityDecoder[F, MinMaxCases] = jsonOf

  given Encoder[MinMaxCases] = Encoder.AsObject.derived[MinMaxCases]

  given[F[_]]: EntityEncoder[F, MinMaxCases] = jsonEncoderOf