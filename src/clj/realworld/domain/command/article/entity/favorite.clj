(ns realworld.domain.command.article.entity.favorite
  (:require [clojure.spec.alpha :as s]
            [realworld.domain.command.article.value :as value]))

(s/def ::favorite (s/keys :req-un [::value/favorite-id
                                   ::value/created-at]))

(defrecord Favorite [favorite-id created-at])

(defn make-favorite [{:keys [favorite-id created-at]}]
  (let [favorite (map->Favorite {:favorite-id favorite-id
                                 :created-at created-at})]
    (when (s/valid? ::favorite favorite)
      favorite)))
  