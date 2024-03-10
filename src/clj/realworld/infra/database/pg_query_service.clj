(ns realworld.infra.database.pg-query-service
  (:require [realworld.domain.query.service :refer [QueryService]]
            [realworld.infra.database.pg-util :refer [from-pgarray]]))

(defn ->article [{:keys [slug
                         title
                         description
                         body
                         tags
                         favorited
                         favorites-count
                         created-at
                         updated-at
                         username
                         bio
                         image
                         following]}]
  {:slug slug
   :title title
   :description  description
   :body body
   :tagList (from-pgarray tags)
   :favorited favorited
   :favoritesCount favorites-count
   :createdAt created-at
   :updatedAt updated-at
   :author {:username username
            :bio bio
            :image image
            :following following}})

(defrecord PgQueryService [query-fn]
  QueryService
  (get-current-user [_ {:keys [actor-id]}]
    (query-fn :find-user-by-id {:id actor-id}))

  (get-profile [_ {:keys [actor-id username]}]
    (if actor-id
      (query-fn :find-profile-with-following {:actor-id actor-id :username username})
      (query-fn :find-profile {:username username})))

  (get-article [_ {:keys [actor-id slug]}]
    (->article (query-fn :get-article {:slug slug})))

  (get-tags [_]
    (from-pgarray (:array-agg (query-fn :get-tags {})))))


