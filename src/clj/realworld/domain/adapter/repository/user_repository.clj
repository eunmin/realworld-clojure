(ns realworld.domain.adapter.repository.user-repository)

(defprotocol UserRepository
  (save [this user])
  (find-by-id [this user-id])
  (find-by-username [this username])
  (find-by-email [this email])
  (follow [this follower-id followee-id])
  (unfollow [this follower-id followee-id])
  (has-following [this follower-id followee-id]))