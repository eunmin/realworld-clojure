(ns realworld.domain.adapter.gateway.token-gateway)

(defprotocol TokenGateway
  (generate [this user-id expires-in-sec])
  (verify [this token]))
