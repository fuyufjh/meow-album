-- table: user --
INSERT INTO user (id, username, password) VALUES (1, 'fuyufjh', MD5('123456'));

-- table: album --
INSERT INTO album (id, owner_id, title, alias) VALUES (1, 1, '测试', 'test');
INSERT INTO album (id, owner_id, title, alias) VALUES (2, 1, '默认', 'default');

-- table: photo --
INSERT INTO photo (album_id, title, text, preview_url, raw_url) VALUES (1, 'Photo Example', '只是一个例子而已', 'https://picsum.photos/1024/768/?image=3', 'https://picsum.photos/512/384/?image=3');
INSERT INTO photo (album_id, text, preview_url, raw_url) VALUES (1, '我只有描述文字而没有标题', 'https://picsum.photos/1024/768/?image=7', 'https://picsum.photos/512/384/?image=7');
INSERT INTO photo (album_id, title, preview_url, raw_url) VALUES (1, '我是标题', 'https://picsum.photos/1024/768/?image=13', 'https://picsum.photos/512/384/?image=13');
INSERT INTO photo (album_id, preview_url, raw_url) VALUES (1, 'https://picsum.photos/1024/768/?image=19', 'https://picsum.photos/512/384/?image=19');
INSERT INTO photo (album_id, title, preview_url, raw_url) VALUES (1, '这是最后一张图', 'https://picsum.photos/1024/768/?image=23', 'https://picsum.photos/512/384/?image=23');
