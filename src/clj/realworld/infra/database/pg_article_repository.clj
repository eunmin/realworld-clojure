(ns realworld.infra.database.pg-article-repository
  (:require [realworld.domain.adapter.repository.article-repository :refer [ArticleRepository]]
            [realworld.infra.manager.pg-tx-manager :refer [UpdateQueryFn]]))

(defn- to-string-array [conn coll]
  (.createArrayOf conn "varchar" (into-array String coll)))

(defrecord PgArticleRepository [conn query-fn]
  UpdateQueryFn
  (update-query-fn [this query-fn]
    (assoc this :query-fn query-fn))

  ArticleRepository
  (save [_ article]
    (query-fn :save-article (update article :tags #(to-string-array (.getConnection conn) %))))

  (find-by-id [_ article-id]
    (query-fn :find-article-by-id {:id article-id}))

  (find-by-slug [_ slug]
    (query-fn :find-article-by-slug {:slug slug}))

  (delete [_ article]
    (query-fn :delete-article {:article-id (:article-id article)})))
