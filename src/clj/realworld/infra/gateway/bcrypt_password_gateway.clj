(ns realworld.infra.gateway.bcrypt-password-gateway
  (:require [buddy.hashers :as hashers]
            [realworld.domain.adapter.gateway.password-gateway :refer [PasswordGateway]]))

(defrecord BcryptPasswordGateway []
  PasswordGateway
  (hash-password [_ password]
    (hashers/derive password))
  (valid-password? [this hashed-password password]))
