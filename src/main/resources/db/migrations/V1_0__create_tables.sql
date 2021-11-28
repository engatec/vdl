CREATE TABLE IF NOT EXISTS history(
    id INTEGER PRIMARY KEY,
    title TEXT,
    url TEXT NOT NULL,
    download_path TEXT NOT NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS queue(
    id INTEGER PRIMARY KEY,
    title TEXT,
    format_id TEXT NOT NULL,
    url TEXT NOT NULL,
    download_path TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS queue_temp_file(
    queue_id INTEGER NOT NULL,
    file_path TEXT NOT NULL,
    FOREIGN KEY (queue_id) REFERENCES queue(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subscription(
   id INTEGER PRIMARY KEY,
   name TEXT NOT NULL,
   url TEXT NOT NULL,
   download_path TEXT NOT NULL,
   created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subscription_processed_items(
    subscription_id INTEGER NOT NULL,
    item_id TEXT NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscription(id) ON UPDATE CASCADE ON DELETE CASCADE
);
