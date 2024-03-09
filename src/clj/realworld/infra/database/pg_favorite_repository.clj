(ns realworld.infra.database.pg-favorite-repository
  (:require [realworld.domain.adapter.repository.favorite-repository :refer [FavoriteRepository]]
            [realworld.infra.manager.pg-tx-manager :refer [UpdateQueryFn]]))

(defrecord PgFavoriteRepository [query-fn]
  UpdateQueryFn
  (update-query-fn [this query-fn]
    (assoc this :query-fn query-fn))

  FavoriteRepository
  (save [_ favorite])
  (find-by-id [_ favorite-id])
  (delete [_ favorite]))