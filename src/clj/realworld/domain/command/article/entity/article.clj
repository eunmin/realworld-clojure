(ns realworld.domain.command.article.entity.article
  (:require [clojure.spec.alpha :as s]
            [realworld.domain.command.article.value :as value :refer [->slug]]))

(s/def ::article (s/keys :req-un [::value/article-id
                                  ::value/slug
                                  ::value/title
                                  ::value/description
                                  :article/body
                                  ::value/tags
                                  ::value/created-at
                                  ::value/updated-at
                                  ::value/favorites-count
                                  ::value/author-id]))

(defrecord Article [article-id
                    slug
                    title
                    description
                    body
                    tags
                    created-at
                    updated-at
                    favorites-count
                    author-id])

(defn make-article [{:keys [article-id title description body tags created-at author-id]}]
  (let [article (map->Article {:article-id article-id
                               :slug (->slug title)
                               :title title
                               :description description
                               :body body
                               :tags tags
                               :created-at created-at
                               :updated-at nil
                               :favorites-count 0
                               :author-id author-id})]
    (when (s/valid? ::article article)
      article)))

(defn update-article [article {:keys [title description body]}]
  (let [article' (merge article
                        {:title (or title (:title article))
                         :description (or description (:description article))
                         :body (or body (:body article))
                         :slug (or (->slug title) (:slug article))})]
    (when (s/valid? ::article article')
      article')))

(defn editable? [article actor-id]
  (= (:author-id article) actor-id))

(defn deletable? [article actor-id]
  (= (:author-id article) actor-id))

(defn increse-favorite-count [article]
  (update article :favorites-count inc))

(defn decrese-favorite-count [article]
  (update article :favorites-count dec))