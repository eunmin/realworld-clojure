(ns realworld.infra.gateway.jwt-token-gateway
  (:require [buddy.sign.jwt :as jwt]
            [buddy.sign.util :as util]
            [realworld.domain.adapter.gateway.token-gateway :refer [TokenGateway]]))

(defrecord JwtTokenGateway [secret]
  TokenGateway
  (generate [_ user-id expires-in-sec]
    (let [now (util/timestamp)]
      (jwt/sign {:user-id user-id
                 :exp (+ now expires-in-sec)} secret)))

  (verify [_ token]
    (try
      (jwt/unsign token secret)
      (catch Exception e
        (println e)
        nil))))