(ns realworld.fixture.password-gateway
  (:require [realworld.domain.adapter.gateway.password-gateway :refer [PasswordGateway]]))

(defn hash-password [_ _]
  (throw (ex-info "Not implemented" {})))

(defn valid-password? [_ _ _]
  (throw (ex-info "Not implemented" {})))

(defrecord FixturePasswordGateway []
  PasswordGateway
  (hash-password [this password] (hash-password this password))
  (valid-password? [this hashed-password password] (valid-password? this hashed-password password)))
