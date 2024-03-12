(ns realworld.fixture.token-gateway
  (:require [realworld.domain.adapter.gateway.token-gateway :refer [TokenGateway]]))

(defn generate [_ _ _]
  (throw (ex-info "Not implemented" {})))

(defn verify [_ _]
  (throw (ex-info "Not implemented" {})))

(defrecord FixtureTokenGateway []
  TokenGateway
  (generate [this user-id expires-in-sec] (generate this user-id expires-in-sec))
  (verify [this token] (verify this token)))
