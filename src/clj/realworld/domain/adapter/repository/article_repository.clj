(ns realworld.domain.adapter.repository.article-repository)

(defprotocol ArticleRepository
  (save [this article])
  (find-by-id [this article-id])
  (find-by-slug [this slug])
  (delete [this article]))