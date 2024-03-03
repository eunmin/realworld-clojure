(ns realworld.domain.command.article.entity.article
  (:require [clojure.spec.alpha :as s]
            #_[realworld.domain.command.article.value]))

(s/def ::article (s/keys :req-un [::id
                                  ::slug
                                  ::title
                                  ::description
                                  ::body
                                  ::tags
                                  ::created-at
                                  ::updated-at
                                  ::favorited
                                  ::favorites-count
                                  ::author-id]))

(s/explain-data ::article {})