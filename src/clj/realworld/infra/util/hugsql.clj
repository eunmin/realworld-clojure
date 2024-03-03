(ns realworld.infra.util.hugsql
  (:require [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [hugsql.adapter :as adapter]
            [hugsql.core :as hugsql]))

(defn result-one-snake->kebab
  [this result options]
  (->> (adapter/result-one this result options)
       (transform-keys ->kebab-case-keyword)))

(defn result-many-snake->kebab
  [this result options]
  (->> (adapter/result-many this result options)
       (map #(transform-keys ->kebab-case-keyword %))))

(defmethod hugsql/hugsql-result-fn :1 [sym]
  'realworld.infra.util.hugsql/result-one-snake->kebab)

(defmethod hugsql/hugsql-result-fn :one [sym]
  'realworld.infra.util.hugsql/result-one-snake->kebab)

(defmethod hugsql/hugsql-result-fn :* [sym]
  'realworld.infra.util.hugsql/result-many-snake->kebab)

(defmethod hugsql/hugsql-result-fn :many [sym]
  'realworld.infra.util.hugsql/result-many-snake->kebab)

(defmethod hugsql/hugsql-result-fn :1 [sym]
  'realworld.infra.util.hugsql/result-one-snake->kebab)
