(ns realworld.infra.database.pg-favorite-repository
  (:require [realworld.domain.adapter.repository.favorite-repository :refer [FavoriteRepository]]
            [realworld.infra.manager.pg-tx-manager :refer [UpdateQueryFn]]))

(defrecord PgFavoriteRepository [query-fn]
  UpdateQueryFn
  (update-query-fn [this query-fn]
    (assoc this :query-fn query-fn))

  FavoriteRepository
  (save [_ favorite]
    (query-fn :save-favorite favorite))

  (find-by-id [_ favorite-id]
    (query-fn :find-favorite-by-id {:user-id (:user-id favorite-id)
                                    :article-id (:article-id favorite-id)}))

  (delete [_ {:keys [favorite-id]}]
    (query-fn :delete-favorite {:user-id (:user-id favorite-id)
                                :article-id (:article-id favorite-id)})))