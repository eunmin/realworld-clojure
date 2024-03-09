(ns realworld.domain.command.article.entity.comment
  (:require [clojure.spec.alpha :as s]
            [realworld.domain.command.article.value :as value]))

(s/def ::comment (s/keys :req-un [::value/comment-id
                                  :comment/body
                                  ::value/created-at
                                  ::value/updated-at
                                  ::value/author-id
                                  ::value/article-id]))

(defrecord Comment [comment-id
                    body
                    created-at
                    updated-at
                    author-id
                    article-id])

(defn make-comment [{:keys [comment-id body created-at author-id article-id]}]
  (let [comment (map->Comment {:comment-id comment-id
                               :body body
                               :created-at created-at
                               :updated-at nil
                               :author-id author-id
                               :article-id article-id})]
    (when (s/valid? ::comment comment)
      comment)))


(defn deletable? [comment actor-id]
  (= (:author-id comment) actor-id))

