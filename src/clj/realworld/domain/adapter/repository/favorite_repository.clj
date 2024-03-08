(ns realworld.domain.adapter.repository.favorite-repository)

(defprotocol FavoriteRepository
  (save [this favorite])
  (find-by-id [this favorite-id])
  (delete [this favorite]))