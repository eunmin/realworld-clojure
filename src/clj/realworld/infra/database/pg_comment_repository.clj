(ns realworld.infra.database.pg-comment-repository
  (:require [realworld.domain.adapter.repository.comment-repository :refer [CommentRepository]]
            [realworld.infra.manager.pg-tx-manager :refer [UpdateQueryFn]]))

(defrecord PgCommentRepository [query-fn]
  UpdateQueryFn
  (update-query-fn [this query-fn]
    (assoc this :query-fn query-fn))

  CommentRepository
  (save [_ comment]
    (pos? (query-fn :save-comment comment)))

  (find-by-id [_ comment-id]
    (query-fn :find-comment-by-id {:comment-id comment-id}))

  (delete [_ comment]
    (pos? (query-fn :delete-comment {:comment-id (:comment-id comment)}))))