(ns realworld.infra.web.controllers.user
  (:require [failjure.core :as f]
            [realworld.domain.command.user.use-case :as user-usecase]
            [realworld.infra.web.routes.utils :refer [route-data]]
            [ring.util.http-response :refer [ok unprocessable-entity]]))

(defn register [req]
  (let [user-use-case (-> (route-data req) :use-cases :user)
        input (-> req :parameters :body)
        command (select-keys input [:email :username :password])
        result (user-usecase/registration user-use-case command)]
    (if (f/ok? result)
      (ok {:user {:email (:email input)
                  :token (:token result)
                  :username (:username input)
                  :bio ""
                  :image nil}})
      (unprocessable-entity {:errors {:body [(name (:message result))]}}))))