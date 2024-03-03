(ns realworld.domain.adapter.gateway.password-gateway)

(defprotocol PasswordGateway
  (hash-password [this password])
  (valid-password? [this hashed-password password]))