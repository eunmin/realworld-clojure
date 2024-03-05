(ns realworld.infra.web.controllers.user
  (:require [failjure.core :as f]
            [realworld.domain.adapter.gateway.token-gateway :as token-gateway]
            [realworld.domain.command.user.use-case :as user-usecase]
            [realworld.domain.query.service :as query-service]
            [realworld.infra.web.routes.utils :refer [route-data]]
            [ring.util.http-response :refer [not-found ok unauthorized
                                             unprocessable-entity]]))

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

(defn authentication [req]
  (let [user-use-case (-> (route-data req) :use-cases :user)
        input (-> req :parameters :body)
        command (select-keys input [:email :password])
        result (user-usecase/authentication user-use-case command)]
    (if (f/ok? result)
      (ok {:user {:email (:email input)
                  :token (:token result)
                  :username (:username result)
                  :bio (:bio result)
                  :image (:image result)}})
      (unprocessable-entity {:errors {:body [(name (:message result))]}}))))

(defn get-current-user [{:keys [token] :as req}]
  (let [{:keys [gateway query-service]} (-> (route-data req))]
    (if-let [user-id (token-gateway/verify (:token-gateway gateway) token)]
      (if-let [user (query-service/get-current-user query-service {:actor-id user-id})]
        (ok {:user {:email (:email user)
                    :token token
                    :username (:username user)
                    :bio (:bio user)
                    :image (:image user)}})
        (not-found {:errors {:body ["User not found"]}}))
      (unauthorized {:errors {:body ["Unauthorized"]}}))))

(defn get-profile [{:keys [token] :as req}]
  (let [{:keys [gateway query-service]} (-> (route-data req))
        user-id (when token
                  (token-gateway/verify (:token-gateway gateway) token))
        username (-> req :parameters :path :username)
        params {:actor-id user-id
                :username username}
        profile (query-service/get-profile query-service params)]
    (if profile
      (ok {:profile profile})
      (not-found {:errors {:body ["Profile not found"]}}))))

(defn follow [{:keys [token] :as req}]
  (let [user-use-case (-> (route-data req) :use-cases :user)
        username (-> req :parameters :path :username)
        command {:token token :username username}
        result (user-usecase/follow-user user-use-case command)]
    (if (f/ok? result)
      (ok {:profile (select-keys result [:username
                                         :bio
                                         :image
                                         :following])})
      (unprocessable-entity {:errors {:body [(name (:message result))]}}))))

(defn unfollow [{:keys [token] :as req}]
  (let [user-use-case (-> (route-data req) :use-cases :user)
        username (-> req :parameters :path :username)
        command {:token token :username username}
        result (user-usecase/unfollow-user user-use-case command)]
    (if (f/ok? result)
      (ok {:profile (select-keys result [:username
                                         :bio
                                         :image
                                         :following])})
      (unprocessable-entity {:errors {:body [(name (:message result))]}}))))