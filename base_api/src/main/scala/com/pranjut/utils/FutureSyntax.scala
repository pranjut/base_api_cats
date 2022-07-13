package com.pranjut.utils

import cats.effect.{ Async, IO }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Either, Failure, Success }

object FutureSyntax {
  implicit class FutureAsync[A](future: => Future[A]) {

    def callback(cb: Either[Throwable, A] => Unit)(implicit ec: ExecutionContext): Unit = {
      future.onComplete {
        case Success(value) => cb(Right(value))
        case Failure(error) => cb(Left(error))
      }
    }

    def asDelayedF[F[_]: Async](implicit ec: ExecutionContext): F[A] = {
      Async[F].async_ {
        cb =>
          callback(cb)
      }
    }
  }

}
