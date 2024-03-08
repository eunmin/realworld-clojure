(ns realworld.domain.command.user.use-case
  (:require [clj-ulid :refer [ulid]]
            [realworld.domain.command.user.entity.user :as user :refer [make-user]]
            [failjure.core :as f :refer [attempt-all]]
            [realworld.domain.adapter.gateway.password-gateway :as password-gateway]
            [realworld.domain.adapter.gateway.token-gateway :as token-gateway]
            [realworld.domain.adapter.repository.user-repository :as user-repository]
            [realworld.domain.command.user.value :refer [make-email
                                                         make-password
                                                         make-username
                                                         token-expires-in-sec]]
            [realworld.domain.util.time :refer [now]]))

(defprotocol UserUsecase
  (registration [this command])
  (authentication [this command])
  (update-user [this command])
  (follow-user [this command])
  (unfollow-user [this command]))

(defrecord UserUseCaseImpl [with-tx password-gateway token-gateway user-repository]
  UserUsecase
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Registration

  (registration [_ {:keys [username email password]}]
    (attempt-all
     [user-id (ulid)
      created-at (now)
      username' (or (make-username username) (f/fail :registration-error/invalid-username))
      email' (or (make-email email) (f/fail :registration-error/invalid-email))
      password' (or (make-password password) (f/fail :registration-error/invalid-password))
      hashed-password (or (password-gateway/hash-password password-gateway password')
                          (f/fail :registration-error/invalid-password))
      _ (with-tx [user-repository]
          (fn [user-repository]
            (attempt-all
             [_ (when-not (nil? (user-repository/find-by-username user-repository username'))
                  (f/fail :registration-error/username-already-exists))
              _ (when-not (nil? (user-repository/find-by-email user-repository email'))
                  (f/fail :registration-error/email-already-exists))
              user (make-user {:user-id user-id
                               :username username'
                               :email email'
                               :hashed-password hashed-password
                               :created-at created-at})]
             (user-repository/save user-repository user))))
      token (token-gateway/generate token-gateway user-id token-expires-in-sec)]
     {:token token}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Authentication

  (authentication [_ {:keys [email password]}]
    (attempt-all
     [email' (or (make-email email) (f/fail :authentication-error/invalid-email))
      password' (or (make-password password) (f/fail :authentication-error/invalid-password))
      user (or (user-repository/find-by-email user-repository email')
               (f/fail :authentication-error/user-not-found))
      _ (when-not (password-gateway/valid-password? password-gateway
                                                    (:hashed-password user)
                                                    password')
          (f/fail :authentication-error/invalid-password))
      token (token-gateway/generate token-gateway (:user-id user) token-expires-in-sec)]
     {:token token
      :username (:username user)
      :bio (:bio user)
      :image (:image user)}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Update User

  ;; TODO: 옵셔널 필드 처리
  (update-user [_ {:keys [token username email password bio image]}]
    (attempt-all
     [username' (when username
                  (or (make-username username) (f/fail :update-user-error/invalid-username)))
      email' (when email
               (or (make-email email) (f/fail :update-user-error/invalid-email)))
      password' (when password
                  (or (make-password password) (f/fail :update-user-error/invalid-password)))
      user-id (or (token-gateway/verify token-gateway token)
                  (f/fail :update-user-error/invalid-token))
      hashed-password (when password'
                        (or (password-gateway/hash-password password-gateway password')
                            (f/fail :update-user-error/invalid-password)))
      user (with-tx [user-repository]
             (fn [user-repository]
               (attempt-all
                [user (or (user-repository/find-by-id user-repository user-id)
                          (f/fail :update-user-error/user-not-found))
                 _ (when (and username'
                              (not= username' (:username user))
                              (not (nil? (user-repository/find-by-username user-repository
                                                                           username'))))
                     (f/fail :update-user-error/username-already-exists))
                 user' (user/update-user user {:username username'
                                               :email email'
                                               :hashed-password hashed-password
                                               :bio bio
                                               :image image})
                 _ (user-repository/save user-repository user')]
                user')))]
     {:token token
      :username (:username user)
      :email (:email user)
      :bio (:bio user)
      :image (:image user)}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Follow User

  (follow-user [_ {:keys [token username]}]
    (attempt-all
     [follower-id (or (token-gateway/verify token-gateway token)
                      (f/fail :follow-user-error/invalid-token))
      username (or (make-username username) (f/fail :follow-user-error/invalid-username))
      user (with-tx [user-repository]
             (fn [user-repository]
               (attempt-all
                [followee (or (user-repository/find-by-username user-repository username)
                              (f/fail :follow-user-error/user-not-found))
                 already-following? (user-repository/has-following user-repository
                                                                   follower-id
                                                                   (:user-id followee))
                 _ (when already-following?
                     (f/fail :follow-user-error/already-following))
                 _ (when (= follower-id (:user-id followee))
                     (f/fail :follow-user-error/cant-follow-self))
                 _ (user-repository/follow user-repository follower-id (:user-id followee))]
                followee)))]
     {:username (:username user)
      :bio (:bio user)
      :image (:image user)
      :following true}))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Unfollow User

  (unfollow-user [_ {:keys [token username]}]
    (attempt-all
     [follower-id (or (token-gateway/verify token-gateway token)
                      (f/fail :unfollow-user-error/invalid-token))
      username (or (make-username username) (f/fail :unfollow-user-error/invalid-username))
      user (with-tx [user-repository]
             (fn [user-repository]
               (attempt-all
                [followee (or (user-repository/find-by-username user-repository username)
                              (f/fail :unfollow-user-error/user-not-found))
                 _ (user-repository/unfollow user-repository follower-id (:user-id followee))]
                followee)))]
     {:username (:username user)
      :bio (:bio user)
      :image (:image user)
      :following false})))