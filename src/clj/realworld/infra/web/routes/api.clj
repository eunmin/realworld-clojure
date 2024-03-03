(ns realworld.infra.web.routes.api
  (:require
   [realworld.infra.web.controllers.health :as health]
   [realworld.infra.web.controllers.user :as user]
   [realworld.infra.web.middleware.exception :as exception]
   [realworld.infra.web.middleware.formats :as formats]
   [integrant.core :as ig]
   [reitit.coercion.spec :as spec]
   [realworld.infra.web.inputs :as inputs]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]

   [reitit.swagger :as swagger]))

(def route-data
  {:coercion   spec/coercion
   :muuntaja   formats/instance
   :swagger    {:id ::api}
   :middleware [;; query-params & form-params
                parameters/parameters-middleware
                  ;; content-negotiation
                muuntaja/format-negotiate-middleware
                  ;; encoding response body
                muuntaja/format-response-middleware
                  ;; exception handling
                coercion/coerce-exceptions-middleware
                  ;; decoding request body
                muuntaja/format-request-middleware
                  ;; coercing request parameters
                coercion/coerce-request-middleware
                  ;; exception handling
                exception/wrap-exception]})

;; Routes
(defn api-routes [_opts]
  [["/swagger.json"
    {:get {:no-doc  true
           :swagger {:info {:title "realworld API"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/health"
    {:get health/healthcheck!}]
   ["/user"
    {:name :api/get-current-user
     :get {:handler (constantly {:status 200})}}]
   ["/users"
    [""
     {:name :api/reigster
      :post {:parameters {:body ::inputs/register}
             :handler user/register}}]
    ["/login"
     {:name :api/authentication
      :post {:parameters {:body ::inputs/authentication}
             :handler user/authentication}}]]])

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path (merge route-data opts) (api-routes opts)])
