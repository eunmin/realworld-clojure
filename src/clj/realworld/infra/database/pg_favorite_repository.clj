(ns realworld.infra.database.pg-favorite-repository
  (:require [realworld.domain.adapter.repository.favorite-repository :refer [FavoriteRepository]]
            [realworld.infra.manager.pg-tx-manager :refer [UpdateQueryFn]]))

(defrecord PgFavoriteRepository [query-fn]
  UpdateQueryFn
  (update-query-fn [this query-fn]
    (assoc this :query-fn query-fn))

  FavoriteRepository
  (save [_ {:keys [favorite-id created-at]}]
    (pos? (query-fn :save-favorite {:user-id (:user-id favorite-id)
                                    :article-id (:article-id favorite-id)
                                    :created-at created-at})))

  (find-by-id [_ favorite-id]
    (let [{:keys [user-id
                  article-id
                  created-at]} (query-fn :find-favorite-by-id
                                         {:user-id (:user-id favorite-id)
                                          :article-id (:article-id favorite-id)})]
      {:favorite-id {:user-id user-id
                     :article-id article-id}
       :created-at created-at}))

  (delete [_ {:keys [favorite-id]}]
    (pos? (query-fn :delete-favorite {:user-id (:user-id favorite-id)
                                      :article-id (:article-id favorite-id)}))))