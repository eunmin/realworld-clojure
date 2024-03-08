(ns realworld.infra.database.pg-query-service
  (:require [realworld.domain.query.service :refer [QueryService]]))

(defrecord PgQueryService [query-fn]
  QueryService
  (get-current-user [_ {:keys [actor-id]}]
    (query-fn :find-user-by-id {:id actor-id}))

  (get-profile [_ {:keys [actor-id username]}]
    (if actor-id
      (query-fn :find-profile-with-following {:actor-id actor-id :username username})
      (query-fn :find-profile {:username username}))))