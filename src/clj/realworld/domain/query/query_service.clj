(ns realworld.domain.query.query-service)

(defprotocol QueryService
  (get-current-user [this params])
  (get-profile [this params])
  (get-article [this params])
  (get-comments [this params])
  (get-tags [this]))