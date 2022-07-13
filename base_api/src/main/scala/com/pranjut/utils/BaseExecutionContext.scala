package com.pranjut.utils

import scala.concurrent.ExecutionContext

object BaseExecutionContext {

  implicit val executionContext: ExecutionContext = concurrent.ExecutionContext.Implicits.global

}
