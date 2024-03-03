(ns realworld.infra.web.inputs
  (:require [clojure.spec.alpha :as s]))

(s/def ::username string?)

(s/def ::email string?)

(s/def ::password string?)

(s/def ::register (s/keys :req-un [::username ::email ::password]))

(s/def ::authentication (s/keys :req-un [::email ::password]))