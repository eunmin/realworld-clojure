(ns realworld.fixture.user-repository
  (:require [realworld.domain.adapter.repository.user-repository :refer [UserRepository]]
            [realworld.infra.manager.pg-tx-manager :refer [UpdateQueryFn]]))

(defn save [_ _]
  (throw (ex-info "Not implemented" {})))

(defn find-by-id [_ _]
  (throw (ex-info "Not implemented" {})))

(defn find-by-username [_ _]
  (throw (ex-info "Not implemented" {})))

(defn find-by-email [_ _]
  (throw (ex-info "Not implemented" {})))

(defn follow [_ _ _]
  (throw (ex-info "Not implemented" {})))

(defn unfollow [_ _ _]
  (throw (ex-info "Not implemented" {})))

(defn has-following [_ _ _]
  (throw (ex-info "Not implemented" {})))

(defrecord FixtureUserRepository []
  UpdateQueryFn
  (update-query-fn [this _] this)

  UserRepository
  (save [this user] (save this user))
  (find-by-id [this user-id] (find-by-id this user-id))
  (find-by-username [this username] (find-by-username this username))
  (find-by-email [this email] (find-by-email this email))
  (follow [this follower-id followee-id] (follow this follower-id followee-id))
  (unfollow [this follower-id followee-id] (unfollow this follower-id followee-id))
  (has-following [this follower-id followee-id] (has-following this follower-id followee-id)))
