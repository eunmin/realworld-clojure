(ns realworld.infra.web.controllers.user
  (:require
   #_[realworld.domain.command.user.use-case :as user-usecase]
   #_[realworld.infra.web.error :refer [error-response]]
   #_[realworld.infra.web.routes.utils :refer [route-data]]
   [failjure.core :as f]
   [reitit.core :as r :refer [match->path match-by-name]]
   [ring.util.http-response :refer [created]]))

(defn register [{:keys [:reitit.core/router] :as req}]
  #_(let [user-use-case (-> (route-data req) :use-cases :user)]
      (f/if-let-ok? [result (user-usecase/register user-use-case (-> req :parameters :body))]
                    (let [url (-> router
                                  (match-by-name :api/get-current-user)
                                  match->path)]
                      (created url (dissoc result :hashed-password)))
                    (error-response result))))