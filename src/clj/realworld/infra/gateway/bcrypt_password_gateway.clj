(ns realworld.infra.gateway.bcrypt-password-gateway
  (:require [buddy.hashers :as hashers]
            [realworld.domain.adapter.gateway.password-gateway :refer [PasswordGateway]]))

(defrecord BcryptPasswordGateway []
  PasswordGateway
  (hash-password [_ password]
    (hashers/derive password))
  (valid-password? [_ hashed-password password]
    (:valid (hashers/verify password hashed-password))))
