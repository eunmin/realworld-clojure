(ns realworld.domain.adapter.repository.article-repository)

(defprotocol ArticleRepository
  (save [this article])
  (find-by-slug [this slug])
  (delete [this article]))