(ns realworld.domain.query.service)

(defprotocol QueryService
  (get-current-user [this params]))