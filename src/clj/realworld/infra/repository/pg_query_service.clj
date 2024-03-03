(ns realworld.infra.repository.pg-query-service
  (:require [realworld.domain.query.service :refer [QueryService]]))

(defrecord PgQueryService [query-fn]
  QueryService
  (get-current-user [_ {:keys [actor-id]}]
    (query-fn :find-user-by-id {:id actor-id})))