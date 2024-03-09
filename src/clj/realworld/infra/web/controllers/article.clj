(ns realworld.infra.web.controllers.article
  (:require [failjure.core :as f]
            [realworld.infra.web.routes.utils :refer [route-data]]
            [realworld.domain.query.service :as query-service]
            [realworld.domain.command.article.use-case :as article-use-case]
            [ring.util.http-response :refer [ok unprocessable-entity]]))

(defn list-articles [{:keys [token] :as req}])

(defn feed-articles [{:keys [token] :as req}])

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
      (unprocessable-entity {:errors {:body [(name (:message result))]}}))))

(defn update-article [{:keys [token] :as req}])

(defn delete-article [{:keys [token] :as req}]
  (let [{:keys [use-cases]} (-> (route-data req))
        slug (-> req :parameters :path :slug)
        result (article-use-case/delete-article (:article use-cases) {:slug slug
                                                                      :token token})]
    (if (f/ok? result)
      (ok {})
      (unprocessable-entity {:errors {:body [(name (:message result))]}}))))

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
      (unprocessable-entity {:errors {:body [(name (:message result))]}}))))

(defn get-comments [{:keys [token] :as req}])

(defn delete-comment [{:keys [token] :as req}]
  (let [{:keys [use-cases]} (-> (route-data req))
        slug (-> req :parameters :path :slug)
        comment-id (-> req :parameters :path :comment-id)
        result (article-use-case/delete-comment (:article use-cases) {:token token
                                                                      :slug slug
                                                                      :comment-id comment-id})]
    (if (f/ok? result)
      (ok {})
      (unprocessable-entity {:errors {:body [(name (:message result))]}}))))

(defn favorite [{:keys [token] :as req}])

(defn unfavorite [{:keys [token] :as req}])

(defn get-tags [{:keys [token] :as req}])
