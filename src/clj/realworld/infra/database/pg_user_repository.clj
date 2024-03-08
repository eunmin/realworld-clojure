(ns realworld.infra.database.pg-user-repository
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
    (query-fn :save-user user))
  (find-by-id [_ id]
    (query-fn :find-user-by-id {:id id}))
  (find-by-username [_ username]
    (query-fn :find-user-by-username {:username username}))
  (find-by-email [_ email]
    (query-fn :find-user-by-email {:email email}))
  (follow [_ follower-id followee-id]
    (query-fn :follow {:follower-id follower-id
                       :followee-id followee-id}))
  (unfollow [_ follower-id followee-id]
    (query-fn :unfollow {:follower-id follower-id
                         :followee-id followee-id}))
  (has-following [_ follower-id followee-id]
    (pos? (:count (query-fn :find-following {:follower-id follower-id
                                             :followee-id followee-id})))))

