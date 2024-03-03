(ns realworld.infra.repository.pg-user-repository
  #_(:require [next.jdbc.date-time]))

;; (defrecord PGUserRepository [query-fn]
;;   Tx
;;   (update-query-fn [this query-fn]
;;     (assoc this :query-fn query-fn))

;;   UserRepository
;;   (create [_ user]
;;     (query-fn :insert-user user))
;;   (find-by-id [_ id]
;;     (query-fn :find-user-by-id {:id id}))
;;   (find-by-username [_ username]
;;     (query-fn :find-user-by-username {:username username}))
;;   (find-by-email [_ email]
;;     (query-fn :find-user-by-email {:email email})))