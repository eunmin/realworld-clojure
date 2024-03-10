(ns realworld.infra.database.pg-article-repository
  (:require [realworld.domain.adapter.repository.article-repository :refer [ArticleRepository]]
            [realworld.infra.database.pg-util :refer [from-pgarray
                                                      to-string-pgarray]]
            [realworld.infra.manager.pg-tx-manager :refer [UpdateQueryFn]]))

(defrecord PgArticleRepository [conn query-fn]
  UpdateQueryFn
  (update-query-fn [this query-fn]
    (assoc this :query-fn query-fn))

  ArticleRepository
  (save [_ article]
    (pos? (query-fn :save-article (update article :tags
                                          #(to-string-pgarray (.getConnection conn) %)))))

  (find-by-slug [_ slug]
    (when-let [article (query-fn :find-article-by-slug {:slug slug})]
      (update article :tags from-pgarray)))

  (delete [_ article]
    (pos? (query-fn :delete-article {:article-id (:article-id article)}))))


