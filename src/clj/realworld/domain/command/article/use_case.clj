(ns realworld.domain.command.article.use-case
  (:require [clj-ulid :refer [ulid]]
            [failjure.core :as f :refer [attempt-all]]
            [realworld.domain.adapter.gateway.token-gateway :as token-gateway]
            [realworld.domain.adapter.repository.article-repository :as article-repository]
            [realworld.domain.adapter.repository.comment-repository :as comment-repository]
            [realworld.domain.adapter.repository.favorite-repository :as favorite-repository]
            [realworld.domain.adapter.repository.user-repository :as user-repository]
            [realworld.domain.command.article.entity.article :as article]
            [realworld.domain.command.article.entity.comment :as comment]
            [realworld.domain.command.article.entity.favorite :as favorite]
            [realworld.domain.command.article.value :refer [make-article-body
                                                            make-comment-body
                                                            make-description
                                                            make-favorite-id
                                                            make-slug make-tag
                                                            make-title]]
            [realworld.domain.util.time :refer [now]]))

(defprotocol ArticleUsecase
  (create-article [this command])
  (update-article [this command])
  (delete-article [this command])
  (add-comments [this command])
  (delete-comment [this command])
  (favorite-article [this command])
  (unfavorite-article [this command]))

(defrecord ArticleUseCaseImpl [with-tx
                               token-gateway
                               user-repository
                               article-repository
                               favorite-repository
                               comment-repository]
  ArticleUsecase
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Create Article

  (create-article [_ {:keys [token title description body tag-list]}]
    (attempt-all
     [article-id (ulid)
      created-at (now)
      author-id (or (token-gateway/verify token-gateway token)
                    (f/fail :create-article-error/invalid-token))
      title' (or (make-title title) (f/fail :create-article-error/invalid-title))
      body' (or (make-article-body body) (f/fail :create-article-error/invalid-body))
      description' (or (make-description description)
                       (f/fail :create-article-error/invalid-description))
      tags (let [tags' (map make-tag tag-list)]
             (if (some nil? tags')
               (f/fail :create-article-error/invalid-tag)
               tags'))
      author (or (user-repository/find-by-id user-repository author-id)
                 (f/fail :create-article-error/author-not-found))
      article (article/make-article {:article-id article-id
                                     :title title'
                                     :description description'
                                     :body body'
                                     :tags tags
                                     :created-at created-at
                                     :author-id author-id})
      _ (with-tx [article-repository]
          (fn [article-repository]
            (article-repository/save article-repository article)))]
     {:slug (:slug article)
      :created-at created-at
      :author-username (:username author)}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Update Article

  (update-article [_ {:keys [token slug title description body]}]
    (attempt-all
     [slug' (or (make-slug slug) (f/fail :update-article-error/invalid-slug))
      title' (or (make-title title) (f/fail :update-article-error/invalid-title))
      description' (or (make-description description)
                       (f/fail :update-article-error/invalid-description))
      body' (or (make-article-body body) (f/fail :update-article-error/invalid-body))
      actor-id (or (token-gateway/verify token-gateway token)
                   (f/fail :update-article-error/invalid-token))
      result (with-tx [article-repository favorite-repository user-repository]
               (fn [article-repository favorite-repository user-repository]
                 (attempt-all
                  [article (or (article-repository/find-by-slug article-repository slug')
                               (f/fail :update-article-error/article-not-found))
                   author (or (user-repository/find-by-id user-repository (:author-id article))
                              (f/fail :update-article-error/author-not-found))
                   article' (or (article/update-article article actor-id
                                                        {:title title'
                                                         :description description'
                                                         :body body'})
                                (f/fail :update-article-error/author-mismatch))
                   _ (article-repository/save article-repository article')
                   favorited (favorite-repository/find-by-id favorite-repository
                                                             (make-favorite-id (:article-id article')
                                                                               actor-id))]
                  {:article article'
                   :author author
                   :favorited favorited})))]
     {:slug (-> result :article :slug)
      :title (-> result :article :title)
      :description (-> result :article :description)
      :body (-> result :article :body)
      :created-at (-> result :article :created-at)
      :updated-at (-> result :article :updated-at)
      :tags (-> result :article :tags)
      :favroites-count (-> result :article :favorites-count)
      :author-username (:username (:author result))
      :favorited (:favorited result)}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Delete Article

  (delete-article [_ {:keys [token slug]}]
    (attempt-all
     [slug' (or (make-slug slug) (f/fail :delete-article-error/invalid-slug))
      actor-id (or (token-gateway/verify token-gateway token)
                   (f/fail :delete-article-error/invalid-token))
      _ (with-tx [article-repository]
          (fn [article-repository]
            (attempt-all
             [article (or (article-repository/find-by-slug article-repository slug')
                          (f/fail :delete-article-error/article-not-found))
              _ (when-not (article/deletable? article actor-id)
                  (f/fail :delete-article-error/author-mismatch))]
             (article-repository/delete article-repository article))))]
     {:slug slug}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Add Comments to an Article

  (add-comments [_ {:keys [token slug body]}]
    (attempt-all
     [slug' (or (make-slug slug) (f/fail :add-comments-error/invalid-slug))
      author-id (or (token-gateway/verify token-gateway token)
                    (f/fail :add-comments-error/invalid-token))
      body' (or (make-comment-body body) (f/fail :add-comments-error/invalid-body))
      comment-id (ulid)
      created-at (now)
      author (with-tx [user-repository article-repository comment-repository]
               (fn [user-repository article-repository comment-repository]
                 (attempt-all
                  [author (or (user-repository/find-by-id user-repository author-id)
                              (f/fail :add-comments-error/author-not-found))
                   article (or (article-repository/find-by-slug article-repository slug')
                               (f/fail :add-comments-error/article-not-found))
                   comment (comment/make-comment {:comment-id comment-id
                                                  :body body'
                                                  :created-at created-at
                                                  :author-id author-id
                                                  :article-id (:article-id article)})
                   _ (comment-repository/save comment-repository comment)]
                  author)))]
     {:comment-id comment-id
      :created-at created-at
      :author-username (:username author)}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Delete Comment

  (delete-comment [_ {:keys [token slug comment-id]}]
    (attempt-all
     [slug' (or (make-slug slug) (f/fail :delete-comment-error/invalid-slug))
      actor-id (or (token-gateway/verify token-gateway token)
                   (f/fail :delete-comment-error/invalid-token))
      _ (with-tx [article-repository comment-repository]
          (fn [article-repository comment-repository]
            (attempt-all
             [_ (or (article-repository/find-by-slug article-repository slug')
                    (f/fail :delete-comment-error/article-not-found))
              comment (comment-repository/find-by-id comment-repository comment-id)
              _ (when-not (comment/deletable? comment actor-id)
                  (f/fail :delete-comment-error/author-mismatch))]
             (comment-repository/delete comment-repository comment))))]
     {:slug slug
      :comment-id comment-id}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Favorite Article

  (favorite-article [_ {:keys [token slug]}]
    (attempt-all
     [slug' (or (make-slug slug) (f/fail :favorite-article-error/invalid-slug))
      actor-id (or (token-gateway/verify token-gateway token)
                   (f/fail :favorite-article-error/invalid-token))
      created-at (now)
      result (with-tx [user-repository article-repository favorite-repository]
               (fn [user-repository article-repository favorite-repository]
                 (attempt-all
                  [article (or (article-repository/find-by-slug article-repository slug')
                               (f/fail :favorite-article-error/article-not-found))
                   actor (or (user-repository/find-by-id user-repository actor-id)
                             (f/fail :favorite-article-error/author-not-found))
                   favorite-id (make-favorite-id (:article-id article) actor-id)
                   favorite (favorite/make-favorite {:favorite-id favorite-id
                                                     :created-at created-at})
                   success (favorite-repository/save favorite-repository favorite)
                   _ (when-not success
                       (f/fail :favorite-article-error/already-favorited))
                   article' (article/increse-favorite-count article)
                   _ (article-repository/save article-repository article')]
                  {:article article'
                   :actor actor})))]
     {:slug slug
      :title (:title (:article result))
      :description (:description (:article result))
      :body (:body (:article result))
      :created-at (:created-at (:article result))
      :updated-at (:updated-at (:article result))
      :tags (:tags (:article result))
      :favorites-count (:favorites-count (:article result))
      :author-username (:username (:actor result))}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Unfavorite Article

  (unfavorite-article [_ {:keys [token slug]}]
    (attempt-all
     [slug' (or (make-slug slug) (f/fail :unfavorite-article-error/invalid-slug))
      actor-id (or (token-gateway/verify token-gateway token)
                   (f/fail :unfavorite-article-error/invalid-token))
      result (with-tx [user-repository article-repository favorite-repository]
               (fn [user-repository article-repository favorite-repository]
                 (attempt-all
                  [article (or (article-repository/find-by-slug article-repository slug')
                               (f/fail :unfavorite-article-error/article-not-found))
                   actor (or (user-repository/find-by-id user-repository actor-id)
                             (f/fail :unfavorite-article-error/author-not-found))
                   favorite-id (make-favorite-id (:article-id article) actor-id)
                   favorite (or (favorite-repository/find-by-id favorite-repository favorite-id)
                                (f/fail :unfavorite-article-error/favorite-not-found))
                   success (favorite-repository/delete favorite-repository favorite)
                   _ (when-not success
                       (f/fail :unfavorite-article-error/not-favorited))
                   article' (article/decrese-favorite-count article)
                   _ (article-repository/save article-repository article')]
                  {:article article'
                   :actor actor})))]
     {:slug slug
      :title (:title (:article result))
      :description (:description (:article result))
      :body (:body (:article result))
      :created-at (:created-at (:article result))
      :updated-at (:updated-at (:article result))
      :tags (:tags (:article result))
      :favorites-count (:favorites-count (:article result))
      :author-username (:username (:actor result))})))