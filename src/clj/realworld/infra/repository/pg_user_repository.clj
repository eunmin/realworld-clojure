(ns realworld.infra.repository.pg-user-repository
  (:require [realworld.infra.util.hugsql]
            [next.jdbc.date-time]
            [realworld.domain.adapter.repository.user-repository :refer [UserRepository]]
            [realworld.infra.manager.pg-tx-manager :refer [UpdateQueryFn]]))

(defrecord PgUserRepository [query-fn]
  UpdateQueryFn
  (update-query-fn [this query-fn]
    (assoc this :query-fn query-fn))

  UserRepository
  (save [_ user]
    (query-fn :save user))
  (find-by-id [_ id]
    (query-fn :find-user-by-id {:id id}))
  (find-by-username [_ username]
    (query-fn :find-user-by-username {:username username}))
  (find-by-email [_ email]
    (query-fn :find-user-by-email {:email email})))