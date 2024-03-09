CREATE TABLE IF NOT EXISTS articles (
  id VARCHAR PRIMARY KEY,
  slug VARCHAR NOT NULL UNIQUE,
  title VARCHAR NOT NULL,
  description VARCHAR NOT NULL,
  body TEXT NOT NULL,
  tags VARCHAR[] NOT NULL,
  author_id VARCHAR NOT NULL,
  favorites_count INT DEFAULT 0,
  created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ
);
--;;
CREATE TABLE IF NOT EXISTS comments (
  id VARCHAR PRIMARY KEY,
  body TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ,
  author_id VARCHAR NOT NULL,
  article_id VARCHAR NOT NULL
);
--;;
CREATE TABLE IF NOT EXISTS favorites (
  user_id VARCHAR NOT NULL,
  article_id VARCHAR NOT NULL,
  created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (user_id, article_id)
);