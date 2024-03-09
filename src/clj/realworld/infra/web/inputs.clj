(ns realworld.infra.web.inputs
  (:require [clojure.spec.alpha :as s]))

(s/def ::username string?)

(s/def ::email string?)

(s/def ::password string?)

(s/def ::register (s/keys :req-un [::username ::email ::password]))

(s/def ::authentication (s/keys :req-un [::email ::password]))

(s/def ::profile (s/keys :req-un [::username]))

(s/def ::title string?)

(s/def ::description string?)

(s/def ::body string?)

(s/def ::tag-list (s/coll-of string?))

(s/def ::create-article (s/keys :req-un [::title ::description ::body ::tag-list]))

(s/def ::add-comments (s/keys :req-un [::body]))