(ns realworld.infra.web.routes.api
  (:require [integrant.core :as ig]
            [realworld.infra.web.controllers.health :as health]
            [realworld.infra.web.controllers.user :as user]
            [realworld.infra.web.controllers.article :as article]
            [realworld.infra.web.inputs :as inputs]
            [realworld.infra.web.middleware.core :refer [wrap-required-token
                                                         wrap-optional-token]]
            [realworld.infra.web.middleware.exception :as exception]
            [realworld.infra.web.middleware.formats :as formats]
            [reitit.coercion.spec :as spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]))

(def route-data
  {:coercion   spec/coercion
   :muuntaja   formats/instance
   :swagger    {:id ::api}
   :middleware [parameters/parameters-middleware
                muuntaja/format-negotiate-middleware
                muuntaja/format-response-middleware
                coercion/coerce-exceptions-middleware
                muuntaja/format-request-middleware
                coercion/coerce-request-middleware
                exception/wrap-exception]})

(def ^:private security {:optional [{} {:apiAuth []}]
                         :required [{:apiAuth []}]})

;; Routes
(defn api-routes [opts]
  [["/swagger.json"
    {:get {:no-doc  true
           :swagger {:info {:title "realworld API"}
                     :securityDefinitions {:apiAuth {:type "apiKey"
                                                     :name "Authorization"
                                                     :in "header"}}}
           :handler (swagger/create-swagger-handler)}}]
   ["/health"
    {:get health/healthcheck!}]
   ["/users"
    ["/login"
     {:post {:name :api/authentication
             :summary "Authentication"
             :parameters {:body ::inputs/authentication}
             :handler user/authentication}}]
    [""
     {:post {:name :api/reigster
             :summary "Registration"
             :parameters {:body ::inputs/register}
             :handler user/register}}]]
   ["/user"
    {:get {:name :api/get-current-user
           :summary "Get Current User"
           :swagger {:security (:required security)}
           :middleware [wrap-required-token]
           :handler user/get-current-user}
     :put {:name :api/update-user
           :summary "Update User"
           :swagger {:security (:required security)}
           :handler identity}}]
   ["/profiles" {:parameters {:path ::inputs/profile}}
    ["/:username" {:get {:name :api/get-profile
                         :summary "Get Profile"
                         :swagger {:security (:optional security)}
                         :middleware [wrap-optional-token]
                         :handler user/get-profile}}]
    ["/:username/follow" {:post {:name :api/follow
                                 :summary "Follow user"
                                 :swagger {:security (:required security)}
                                 :middleware [wrap-required-token]
                                 :handler user/follow}}]
    ["/:username/unfollow" {:post {:name :api/unfollow
                                   :summary "Unfollow user"
                                   :swagger {:security (:required security)}
                                   :middleware [wrap-required-token]
                                   :handler user/unfollow}}]]
   ["/articles"
    ["" {:get {:name :api/list-articles
               :summary "List Articles"
               :swagger {:security (:optional security)}
               :middleware [wrap-optional-token]
               :handler article/list-articles}
         :post {:name :api/create-article
                :summary "Create Articles"
                :swagger {:security (:required security)}
                :parameters {:body ::inputs/create-article}
                :middleware [wrap-required-token]
                :handler article/create-article}}]
    ["/feed" {:get {:name :api/feed-articles
                    :summary "Feed Article"
                    :swagger {:security (:required security)}
                    :middleware [wrap-required-token]
                    :handler article/feed-articles}}]
    ["/:slug"
     ["" {:get {:name :api/get-article
                :summary "Get Article"
                :handler article/get-article}
          :put {:name :api/update-article
                :summary "Update Article"
                :swagger {:security (:required security)}
                :middleware [wrap-required-token]
                :handler article/update-article}
          :delete {:name :api/delete-article
                   :summary "Delete Article"
                   :swagger {:security (:required security)}
                   :middleware [wrap-required-token]
                   :handler article/delete-article}}]
     ["/comments"
      ["" {:get {:name :api/get-comments
                 :summary "Get Comments from an Article"
                 :swagger {:security (:optional security)}
                 :middleware [wrap-optional-token]
                 :handler article/get-comments}
           :post {:name :api/add-comment
                  :summary "Add Comments to an Article"
                  :swagger {:security (:required security)}
                  :middleware [wrap-required-token]
                  :handler article/add-comment}}]
      ["/:comment-id" {:delete {:name :api/delete-comment
                                :summary "Delete Comment"
                                :swagger {:security (:required security)}
                                :middleware [wrap-required-token]
                                :handler article/delete-comment}}]]
     ["/favorite" {:post {:name :api/favorite-article
                          :summary "Favorite Article"
                          :swagger {:security (:required security)}
                          :middleware [wrap-required-token]
                          :handler article/favorite}
                   :delete {:name :api/unfavorite-article
                            :summary "Unfavorite Article"
                            :swagger {:security (:required security)}
                            :middleware [wrap-required-token]
                            :handler article/unfavorite}}]]]
   ["/tags" {:get {:name :api/list-tags
                   :summary "Get Tags"
                   :handler article/get-tags}}]])

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path (merge route-data opts) (api-routes opts)])
