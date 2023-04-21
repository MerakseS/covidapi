package com.innowise.covidapi.model

import cats.effect.Concurrent
import cats.implicits.*
import com.innowise.covidapi.model.CovidCases
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*

case class CovidCases(Country: String, Cases: Int, Date: String)

object CovidCases:
  given Decoder[CovidCases] = Decoder.derived[CovidCases]

  given[F[_] : Concurrent]: EntityDecoder[F, CovidCases] = jsonOf

  given Encoder[CovidCases] = Encoder.AsObject.derived[CovidCases]

  given[F[_]]: EntityEncoder[F, CovidCases] = jsonEncoderOf

  given Decoder[List[CovidCases]] = Decoder.decodeList[CovidCases]

  given[F[_] : Concurrent]: EntityDecoder[F, List[CovidCases]] = jsonOf

  given Encoder[List[CovidCases]] = Encoder.encodeList[CovidCases]

  given[F[_]]: EntityEncoder[F, List[CovidCases]] = jsonEncoderOf
