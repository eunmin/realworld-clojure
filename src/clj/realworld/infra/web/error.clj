(ns realworld.infra.web.error)

(def messages
  {:unauthorized "인증되지 않은 사용자입니다. 로그인 후 다시 시도해주세요."
   :user-not-found "사용자를 찾을 수 없습니다."
   :profile-not-found "프로필을 찾을 수 없습니다."
   :invalid-email "잘못된 이메일 주소입니다. 이메일 주소를 확인해주세요."
   :invalid-username "잘못된 username 입니다. username은 최소 3자 이상, 최대 20자 이하로 입력해주세요."
   :invalid-password "비밀번호에는 최소 12자 이상, 최소 하나의 문자, 숫자, 특수문자가 포함되어야 합니다."
   :username-already-exists "이미 사용 중인 username 입니다."
   :email-already-exists "이미 사용 중인 이메일 입니다."})

(def errors
  {:controller/unauthorized (:unauthorized messages)
   :controller/user-not-found (:user-not-found messages)
   :controller/profile-not-found (:profile-not-found messages)
   :registration-error/invalid-username (:invalid-username messages)
   :registration-error/invalid-email (:invalid-email messages)
   :registration-error/invalid-password (:invalid-password messages)
   :registration-error/username-already-exists (:username-already-exists messages)
   :registration-error/email-already-exists (:email-already-exists messages)

   :authentication-error/invalid-email (:invalid-email messages)
   :authentication-error/invalid-password (:invalid-password messages)
   :authentication-error/user-not-found (:user-not-found messages)

   :update-user-error/invalid-username (:invalid-username messages)
   :update-user-error/invalid-email (:invalid-email messages)
   :update-user-error/invalid-password (:invalid-password messages)
   :update-user-error/invalid-token (:unauthorized messages)
   :update-user-error/user-not-found (:user-not-found messages)
   :update-user-error/username-already-exists (:username-already-exists messages)

   :follow-user-error/invalid-token nil
   :follow-user-error/invalid-username nil
   :follow-user-error/user-not-found "팔로우할 사용자를 찾을 수 없습니다."
   :follow-user-error/already-following nil
   :follow-user-error/cant-follow-self nil

   :unfollow-user-error/invalid-token nil
   :unfollow-user-error/invalid-username nil
   :unfollow-user-error/user-not-found "팔로우를 취소할 사용자를 찾을 수 없습니다."

   :create-article-error/invalid-token nil
   :create-article-error/invalid-title nil
   :create-article-error/invalid-body nil
   :create-article-error/invalid-description nil
   :create-article-error/invalid-tag nil
   :create-article-error/author-not-found nil

   :update-article-error/invalid-slug nil
   :update-article-error/invalid-title nil
   :update-article-error/invalid-description nil
   :update-article-error/invalid-body nil
   :update-article-error/invalid-token nil
   :update-article-error/article-not-found nil
   :update-article-error/author-not-found nil
   :update-article-error/edit-permission-denied nil
   :update-article-error/invalid-article nil

   :delete-article-error/invalid-slug nil
   :delete-article-error/invalid-token nil
   :delete-article-error/article-not-found nil
   :delete-article-error/delete-permission-denied nil

   :add-comments-error/invalid-slug nil
   :add-comments-error/invalid-token nil
   :add-comments-error/invalid-body nil

   :add-comments-error/author-not-found nil
   :add-comments-error/article-not-found nil

   :delete-comment-error/invalid-slug nil
   :delete-comment-error/invalid-token nil
   :delete-comment-error/article-not-found nil
   :delete-comment-error/delete-permission-denied nil

   :favorite-article-error/invalid-slug nil
   :favorite-article-error/invalid-token nil
   :favorite-article-error/article-not-found nil
   :favorite-article-error/author-not-found nil
   :favorite-article-error/already-favorited nil

   :unfavorite-article-error/invalid-slug nil
   :unfavorite-article-error/invalid-token nil
   :unfavorite-article-error/article-not-found nil
   :unfavorite-article-error/author-not-found nil
   :unfavorite-article-error/favorite-not-found nil
   :unfavorite-article-error/not-favorited nil

   :default "예상하지 못한 에러가 발생했습니다. 잠시 후 다시 시도해주세요."})

(defn ->errors [fail]
  (let [error-key (or (:message fail) :default)]
    {:errors {:body [(get errors error-key)]}}))
