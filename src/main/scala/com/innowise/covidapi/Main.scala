package com.innowise.covidapi

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = CovidApiServer.run[IO]
