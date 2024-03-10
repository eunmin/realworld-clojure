(ns realworld.domain.command.user.value
  (:require [clojure.spec.alpha :as s]
            [miner.strgen :as sg]))

(s/def ::token string?)

(s/def ::user-id string?)

(def token-expires-in-sec (* 60 60 24))

(s/def ::username (s/and string? #(> (count %) 3) #(< (count %) 128)))

(defn make-username [username]
  (when (s/valid? ::username username)
    username))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")

(s/def ::email (s/spec (s/and string? #(re-matches email-regex %))
                       :gen #(sg/string-generator email-regex)))

(defn make-email [email]
  (when (s/valid? ::email email)
    email))

(def password-regex #"^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$")

(s/def ::password (s/and string? #(re-matches password-regex %)))

(defn make-password [password]
  (when (s/valid? ::password password)
    password))

(s/def ::hashed-password string?)

(s/def ::bio string?)

(def empty-bio "")

(s/def ::image (s/nilable string?))

(s/def ::created-at inst?)

(s/def ::updated-at (s/nilable inst?))
