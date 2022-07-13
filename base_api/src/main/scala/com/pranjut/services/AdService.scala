package com.pranjut.services

import cats.effect.Async
import com.pranjut.db.models.Ad
import com.pranjut.db.repositories.AdsRepo

class AdService[F[_]: Async](adRepo: AdsRepo[F]) {

  def insert(ad: Ad): F[Ad] = adRepo.insert(ad)
  def delete(id: Long): F[Int] = adRepo.delete(id)
  def get(id: Long): F[Option[Ad]] = adRepo.get(id)

}
