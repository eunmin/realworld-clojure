(ns realworld.infra.dev-middleware)

(defn wrap-dev [handler _opts]
  (-> handler))
