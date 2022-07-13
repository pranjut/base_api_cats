package com.pranjut.db.repositories

import cats.effect.Async
import com.pranjut.db.actions.AdAction
import com.pranjut.db.models.Ad
import com.pranjut.db.slick.{ DBComponent, SlickRepository }

import scala.concurrent.{ ExecutionContext, Future }

trait AdsRepo[F[_]] {
  def get(id: Long): F[Option[Ad]]
  def insert(ad: Ad): F[Ad]
  def delete(id: Long): F[Int]
}

class AdsRepoFuture(adAction: AdAction)(implicit ex: ExecutionContext, dbComp: DBComponent) extends AdsRepo[Future] with SlickRepository {
  import adAction._
  import jdbcProfile.api._

  override val dBComponent: DBComponent = dbComp

  def get(id: Long): Future[Option[Ad]] = {
    db.run(tableQuery.filter(_.id === id).result).map(_.headOption)
  }

  def insert(ad: Ad): Future[Ad] = {
    val query = tableQuery.returning(tableQuery.map(primaryColumn)) += ad
    db.run(query).map(insertedId => ad.copy(id = insertedId))
  }

  def delete(id: Long): Future[Int] = {
    val query = tableQuery.filter(_.id === id).delete
    db.run(query)
  }
}

class AsyncAdsRepo[F[_]: Async](adsRepoFut: AdsRepoFuture) extends AdsRepo[F] {

  import com.pranjut.utils.BaseExecutionContext.executionContext
  import com.pranjut.utils.FutureSyntax._

  def get(id: Long): F[Option[Ad]] = adsRepoFut.get(id).asDelayedF
  def insert(ad: Ad): F[Ad] = adsRepoFut.insert(ad).asDelayedF
  def delete(id: Long): F[Int] = adsRepoFut.delete(id).asDelayedF
}