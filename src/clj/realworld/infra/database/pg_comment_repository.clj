(ns realworld.infra.database.pg-comment-repository
  (:require [realworld.domain.adapter.repository.comment-repository :refer [CommentRepository]]
            [realworld.infra.manager.pg-tx-manager :refer [UpdateQueryFn]]))

(defrecord PgCommentRepository [query-fn]
  UpdateQueryFn
  (update-query-fn [this query-fn]
    (assoc this :query-fn query-fn))

  CommentRepository
  (save [_ comment])
  (find-by-id [_ comment-id])
  (delete [_ comment]))