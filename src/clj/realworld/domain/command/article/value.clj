(ns realworld.domain.command.article.value
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::slug string?)

(defn make-slug [slug]
  slug)

(defn ->slug [title]
  (-> title
      (str/lower-case)
      (str/replace #" " "-")))

(s/def ::title string?)

(defn make-title [title]
  title)

(s/def ::description string?)

(defn make-description [description]
  description)

(s/def :article/body string?)

(defn make-article-body [body]
  body)

(s/def :comment/body string?)

(defn make-comment-body [body]
  body)

(s/def ::tag string?)

(defn make-tag [tag]
  tag)

(s/def ::article-id string?)

(s/def ::user-id string?)

(s/def ::favorite-id (s/keys :req-un [::article-id ::user-id]))

(defn make-favorite-id [article-id user-id]
  {:article-id article-id
   :user-id user-id})