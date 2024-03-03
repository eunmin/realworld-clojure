(ns realworld.infra.web.routes.api
  (:require [integrant.core :as ig]
            [realworld.infra.web.controllers.health :as health]
            [realworld.infra.web.controllers.user :as user]
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
    [""
     {:name :api/reigster
      :post {:parameters {:body ::inputs/register}
             :handler user/register}}]
    ["/login"
     {:name :api/authentication
      :post {:parameters {:body ::inputs/authentication}
             :handler user/authentication}}]]
   ["/user"
    {:name :api/get-current-user
     :get {:swagger {:security [{:apiAuth []}]}
           :handler user/get-current-user
           :middleware [wrap-required-token]}}]])

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path (merge route-data opts) (api-routes opts)])
