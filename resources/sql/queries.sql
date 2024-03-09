-- :name save-user :! :n
INSERT INTO users (id, username, email, hashed_password, bio, image, created_at) 
VALUES (:user-id, :username, :email, :hashed-password, :bio, :image, :created-at)
ON CONFLICT (id) DO UPDATE SET
  username = :username,
  email = :email,
  hashed_password = :hashed-password,
  bio = :bio,
  image = :image,
  updated_at = now()

-- :name find-user-by-id :? :1
SELECT id as user_id, * FROM users WHERE id = :id

-- :name find-user-by-username :? :1
SELECT id as user_id, * FROM users WHERE username = :username

-- :name find-user-by-email :? :1
SELECT id as user_id, * FROM users WHERE email = :email

-- :name find-profile-with-following :? :1 
SELECT username, bio, image, 
  CASE WHEN f.created_at IS null THEN false ELSE true END following 
FROM users u 
LEFT JOIN followings f ON u.id = f.following_id AND f.user_id = :actor-id
WHERE username = :username

-- :name find-profile :? :1
SELECT username, bio, image , false as following FROM users WHERE username = :username

-- :name follow :! :n
INSERT INTO followings (user_id, following_id) VALUES (:follower-id, :followee-id)

-- :name unfollow :! :n
DELETE FROM followings WHERE user_id = :follower-id AND following_id = :followee-id

-- :name find-following :? :1
SELECT COUNT(*) FROM followings WHERE user_id = :follower-id AND following_id = :followee-id

-- :name save-article :! :n
INSERT INTO articles (id, slug, title, description, body, tags, author_id, favorites_count, created_at, updated_at)
VALUES (:article-id, :slug, :title, :description, :body, :tags, :author-id, :favorites-count, :created-at, :updated-at)
ON CONFLICT (id) DO UPDATE SET
  slug = :slug,
  title = :title,
  description = :description,
  body = :body,
  tags = :tags,
  author_id = :author-id,
  favorites_count = :favorites-count,
  updated_at = now()

-- :name find-article-by-id :? :1
SELECT id as article_id, * FROM articles WHERE id = :article-id

-- :name find-article-by-slug :? :1
SELECT id as article_id, * FROM articles WHERE slug = :slug

-- :name delete-article :! :n
DELETE FROM articles WHERE id = :article-id

-- :name save-comment :! :n
INSERT INTO comments (id, body, article_id, author_id, created_at, updated_at)
VALUES (:comment-id, :body, :article-id, :author-id, :created-at, :updated-at)
ON CONFLICT (id) DO UPDATE SET
  body = :body,
  article_id = :article-id,
  author_id = :author-id,
  updated_at = now()

-- :name find-comment-by-id :? :1
SELECT id as comment_id, * FROM comments WHERE id = :comment-id

-- :name delete-comment :! :n
DELETE FROM comments WHERE id = :comment-id

-- :name save-favorite :! :n
INSERT INTO favorites (user_id, article_id, created_at)
VALUES (:user-id, :article-id, :created-at)
ON CONFLICT (user_id, article_id) DO NOTHING

-- :name find-favorite-by-id :? :1
SELECT * FROM favorites WHERE user_id = :user-id AND article_id = :article-id

-- :name delete-favorite :! :n
DELETE FROM favorites WHERE user_id = :user-id AND article_id = :article-id

-- :name get-article :? :1
SELECT a.slug, a.title, a.description, a.body, a.tags, a.created_at, a.updated_at, 
  false as favorited, a.favorites_count, u.username, u.bio, u.image, false  as following
FROM articles a 
LEFT JOIN users u ON a.author_id = u.id 
WHERE a.slug = :slug