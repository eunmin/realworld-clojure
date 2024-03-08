(ns realworld.domain.adapter.repository.comment-repository)

(defprotocol CommentRepository
  (save [this comment])
  (find-by-id [this comment-id])
  (delete [this comment]))