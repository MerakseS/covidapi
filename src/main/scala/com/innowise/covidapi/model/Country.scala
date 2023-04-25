package com.innowise.covidapi.model

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.{Decoder, DecodingFailure, Encoder}
import org.http4s.*
import org.http4s.circe.*

case class Country(name: String, slug: String, iso2: String)

object Country:
  given Decoder[Country] = Decoder.instance { h =>
    for {
      name <- h.get[String]("Country")
      slug <- h.get[String]("Slug")
      iso2 <- h.get[String]("ISO2")
    } yield Country(name, slug, iso2)
  }

  given[F[_] : Concurrent]: EntityDecoder[F, Country] = jsonOf

  given Encoder[Country] = Encoder.AsObject.derived[Country]

  given[F[_]]: EntityEncoder[F, Country] = jsonEncoderOf

  given Decoder[List[Country]] = Decoder.decodeList[Country]

  given[F[_] : Concurrent]: EntityDecoder[F, List[Country]] = jsonOf

  given Encoder[List[Country]] = Encoder.encodeList[Country]

  given[F[_]]: EntityEncoder[F, List[Country]] = jsonEncoderOf
