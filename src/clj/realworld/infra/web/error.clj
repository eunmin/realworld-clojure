(ns realworld.infra.web.error
  (:require [ring.util.http-response :refer [bad-request internal-server-error]]))

(defn error-response [{:keys [message]}]
  (let [error-type (:type message)]
    (case error-type
      :invalid-password
      (bad-request {:code error-type
                    :message "비밀번호에는 최소 12자 이상, 최소 하나의 문자, 숫자, 특수문자가 포함되어야 합니다."})

      :invalid-user
      (bad-request {:code error-type
                    :message "올바른 아이디나 이메일을 넣어주세요."})

      :user-exists
      (bad-request {:code error-type
                    :message "이미 사용 중인 아이디거나 이메일입니다."})

      (do
        (println message)
        (internal-server-error {:code :server-error
                                :message "예상하지 못한 서버 에러 입니다. 잠시 후 다시 사용해주세요."})))))