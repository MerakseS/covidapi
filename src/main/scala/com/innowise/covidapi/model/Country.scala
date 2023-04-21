package com.innowise.covidapi.model

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*

case class Country(Country: String, Slug: String, ISO2: String)

object Country:
  given Decoder[Country] = Decoder.derived[Country]

  given[F[_] : Concurrent]: EntityDecoder[F, Country] = jsonOf

  given Encoder[Country] = Encoder.AsObject.derived[Country]

  given[F[_]]: EntityEncoder[F, Country] = jsonEncoderOf

  given Decoder[List[Country]] = Decoder.decodeList[Country]

  given[F[_] : Concurrent]: EntityDecoder[F, List[Country]] = jsonOf

  given Encoder[List[Country]] = Encoder.encodeList[Country]

  given[F[_]]: EntityEncoder[F, List[Country]] = jsonEncoderOf
