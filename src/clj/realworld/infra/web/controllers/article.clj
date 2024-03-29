(ns realworld.infra.web.controllers.article
  (:require [failjure.core :as f]
            [realworld.domain.adapter.gateway.token-gateway :as token-gateway]
            [realworld.domain.command.article.use-case :as article-use-case]
            [realworld.domain.query.query-service :as query-service]
            [realworld.infra.web.error :refer [->errors]]
            [realworld.infra.web.routes.utils :refer [route-data]]
            [ring.util.http-response :refer [ok unprocessable-entity]]))

(defn list-articles [{:keys [token] :as req}]
  (throw (ex-info "Not implemented" {})))

(defn feed-articles [{:keys [token] :as req}]
  (throw (ex-info "Not implemented" {})))

(defn get-article [{:keys [token] :as req}]
  (let [{:keys [query-service]} (-> (route-data req))
        slug (-> req :parameters :path :slug)]
    (ok {:article (query-service/get-article query-service {:slug slug})})))

(defn create-article [{:keys [token] :as req}]
  (let [{:keys [use-cases query-service]} (-> (route-data req))
        article-use-case (:article use-cases)
        input (-> req :parameters :body :article)
        command (assoc (select-keys input [:title :description :body :tag-list]) :token token)
        result (article-use-case/create-article article-use-case command)]
    (if (f/ok? result)
      (let [author (query-service/get-profile query-service {:username (:author-username result)})]
        (ok {:article {:slug (:slug result)
                       :title (:title input)
                       :description (:description input)
                       :body (:body input)
                       :tag-list (:tag-list input)
                       :created-at (:created-at result)
                       :updated-at nil
                       :favorited false
                       :favorites-count 0
                       :author author}}))
      (unprocessable-entity (->errors result)))))

(defn update-article [{:keys [token] :as req}]
  (let [{:keys [use-cases query-service]} (-> (route-data req))
        slug (-> req :parameters :path :slug)
        input (select-keys (-> req :parameters :body :article) [:title :description :body])
        command (merge {:token token :slug slug} input)
        result (article-use-case/update-article (:article use-cases) command)]
    (if (f/ok? result)
      (let [author (query-service/get-profile query-service {:username (:author-username result)})]
        (ok {:article {:slug (:slug result)
                       :title (:title result)
                       :description (:description result)
                       :body (:body result)
                       :tag-list (:tags result)
                       :created-at (:created-at result)
                       :updated-at (:updated-at result)
                       :favorited (:favorited result)
                       :favorites-count (:favorites-count result)
                       :author author}}))
      (unprocessable-entity (->errors result)))))

(defn delete-article [{:keys [token] :as req}]
  (let [{:keys [use-cases]} (-> (route-data req))
        slug (-> req :parameters :path :slug)
        result (article-use-case/delete-article (:article use-cases) {:slug slug
                                                                      :token token})]
    (if (f/ok? result)
      (ok {})
      (unprocessable-entity (->errors result)))))

(defn add-comments [{:keys [token] :as req}]
  (let [{:keys [use-cases query-service]} (-> (route-data req))
        slug (-> req :parameters :path :slug)
        input (-> req :parameters :body :comment)
        command (merge {:token token :slug slug} (select-keys input [:body]))
        result (article-use-case/add-comments (:article use-cases) command)]
    (if (f/ok? result)
      (let [author (query-service/get-profile query-service {:username (:author-username result)})]
        (ok {:comment-id (:comment-id result)
             :body (:body input)
             :createdAt (:created-at result)
             :updatedAt nil
             :author author}))
      (unprocessable-entity (->errors result)))))

(defn get-comments [{:keys [token] :as req}]
  (let [{:keys [query-service gateway]} (-> (route-data req))
        slug (-> req :parameters :path :slug)
        user-id (token-gateway/verify (:token-gateway gateway) token)]
    (ok {:comments (query-service/get-comments query-service {:actor-id user-id
                                                              :slug slug})})))

(defn delete-comment [{:keys [token] :as req}]
  (let [{:keys [use-cases]} (-> (route-data req))
        slug (-> req :parameters :path :slug)
        comment-id (-> req :parameters :path :comment-id)
        result (article-use-case/delete-comment (:article use-cases) {:token token
                                                                      :slug slug
                                                                      :comment-id comment-id})]
    (if (f/ok? result)
      (ok {})
      (unprocessable-entity (->errors result)))))

(defn favorite [{:keys [token] :as req}]
  (let [{:keys [use-cases query-service]} (-> (route-data req))
        slug (-> req :parameters :path :slug)
        result (article-use-case/favorite-article (:article use-cases) {:token token
                                                                        :slug slug})]
    (if (f/ok? result)
      (let [author (query-service/get-profile query-service {:username (:author-username result)})]
        (ok {:article {:slug slug
                       :title (:title result)
                       :description (:description result)
                       :body (:body result)
                       :tag-list (:tag-list result)
                       :created-at (:created-at result)
                       :updated-at (:updated-at result)
                       :favorited true
                       :favorites-count (:favorites-count result)
                       :author author}}))
      (unprocessable-entity (->errors result)))))

(defn unfavorite [{:keys [token] :as req}]
  (let [{:keys [use-cases query-service]} (-> (route-data req))
        slug (-> req :parameters :path :slug)
        result (article-use-case/unfavorite-article (:article use-cases) {:token token
                                                                          :slug slug})]
    (if (f/ok? result)
      (let [author (query-service/get-profile query-service {:username (:author-username result)})]
        (ok {:article {:slug slug
                       :title (:title result)
                       :description (:description result)
                       :body (:body result)
                       :tag-list (:tag-list result)
                       :created-at (:created-at result)
                       :updated-at (:updated-at result)
                       :favorited false
                       :favorites-count (:favorites-count result)
                       :author author}}))
      (unprocessable-entity (->errors result)))))

(defn get-tags [req]
  (let [{:keys [query-service]} (-> (route-data req))]
    (ok {:article (query-service/get-tags query-service)})))
