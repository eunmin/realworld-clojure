(ns realworld.infra.manager.pg-tx-manager
  (:require [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defprotocol Tx
  (update-query-fn [this query-fn]))

(defmethod ig/init-key :db/tx [_ {:keys [conn query-fn]}]
  (fn [repos f]
    (jdbc/with-transaction [tx conn]
      (apply f (map #(update-query-fn % (partial query-fn tx)) repos)))))