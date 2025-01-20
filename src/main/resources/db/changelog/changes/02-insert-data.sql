--liquibase formatted sql

--changeset application:2
INSERT INTO app_user
(id, "password", "role", username)
VALUES('2e1273f4-f574-4807-929e-d7c002643981'::uuid, 'password', 'USER', 'user');
INSERT INTO app_user
(id, "password", "role", username)
VALUES('4fb4e9a8-2d65-4313-9e36-776c4b560b7c'::uuid, 'password', 'ADMIN', 'admin');


INSERT INTO book
(id, author, image, title)
VALUES('50811f5f-33ad-40b4-b775-f055212bcb74'::uuid, 'Jon Bodner', 'https://cf-assets2.tenlong.com.tw/ig/027/850/044/9781098139292.jpg?1708999490', 'Learning Go');
INSERT INTO book
(id, author, image, title)
VALUES('fedee5f4-1cdb-4613-b9a3-1f9e07bbda25'::uuid, 'Stefan Baumgartner', 'https://cf-assets2.tenlong.com.tw/ig/027/177/602/9781098136659.jpg?1708998208', 'Typescript Cookbook');
INSERT INTO book
(id, author, image, title)
VALUES('40b35a5f-c3d7-412a-a3fe-4f6452bc72ad'::uuid, 'Brendon Bums', 'https://cf-assets2.tenlong.com.tw/ig/027/876/992/9781098142162.jpg?1708998644', 'Kubernetes Best Practices');
INSERT INTO book
(id, author, image, title)
VALUES('5f6ac41d-736e-48ea-893a-8b5f9b138e67'::uuid, 'Ben Weidig', 'https://cf-assets2.tenlong.com.tw/ig/026/105/550/9781098109929.jpg?1708998698', 'A Functional Approach to Java');
INSERT INTO book
(id, author, image, title)
VALUES('625e265c-30fb-4dc8-98a6-c5e1734a4e08'::uuid, 'Yevgeniy Brikman', 'https://cf-assets2.tenlong.com.tw/ig/025/803/605/9781098116743.jpg?1708998744', 'Terraform: Up and Running');
INSERT INTO book
(id, author, image, title)
VALUES('703bcc77-2b38-422e-90e6-fd60e7ea8743'::uuid, 'Fregly, Chris', 'https://cf-assets2.tenlong.com.tw/ig/023/084/297/9781492079392.jpg?1709006439', 'Data Science on AWS');
INSERT INTO book
(id, author, image, title)
VALUES('134c2961-8a6a-460e-befa-0ca975005e2c'::uuid, 'Mezzalira, Luca', 'https://cf-assets2.tenlong.com.tw/ig/024/256/434/9781492082996.jpg?1709005907', 'Building Micro-Frontends');
INSERT INTO book
(id, author, image, title)
VALUES('1b1248ca-14e0-4dc9-a0e7-e1eabdf02c36'::uuid, 'Griffiths, David', 'https://cf-assets2.tenlong.com.tw/ig/024/158/198/9781492085843.jpg?1709007417', 'React Cookbook');


INSERT INTO inventory(id, book_id) VALUES('dc6647bb-e521-4089-b907-c6ba76829137'::uuid,'50811f5f-33ad-40b4-b775-f055212bcb74'::uuid);
INSERT INTO inventory(id, book_id) VALUES('2ccdcd73-d148-4220-90af-6e4767bba4c1'::uuid,'50811f5f-33ad-40b4-b775-f055212bcb74'::uuid);
INSERT INTO inventory(id, book_id) VALUES('52cd637e-1c75-4630-8df4-ac5c372f5892'::uuid,'50811f5f-33ad-40b4-b775-f055212bcb74'::uuid);
INSERT INTO inventory(id, book_id) VALUES('acbdf7fa-fedf-47ee-9726-6b6af7e6590c'::uuid,'fedee5f4-1cdb-4613-b9a3-1f9e07bbda25'::uuid);
INSERT INTO inventory(id, book_id) VALUES('c3c254a7-a502-49ec-ab28-9a3305b7e4ae'::uuid,'fedee5f4-1cdb-4613-b9a3-1f9e07bbda25'::uuid);
INSERT INTO inventory(id, book_id) VALUES('61a32b4f-f5bb-4f8d-8eb1-f08997d956c9'::uuid,'fedee5f4-1cdb-4613-b9a3-1f9e07bbda25'::uuid);
INSERT INTO inventory(id, book_id) VALUES('ad29b922-afca-4e89-bcfb-1bded0a8ea3e'::uuid,'40b35a5f-c3d7-412a-a3fe-4f6452bc72ad'::uuid);
INSERT INTO inventory(id, book_id) VALUES('5dc40a49-136e-4e09-bda4-1beff42f516d'::uuid,'40b35a5f-c3d7-412a-a3fe-4f6452bc72ad'::uuid);
INSERT INTO inventory(id, book_id) VALUES('23a5edf4-08cf-4327-8cac-f1c540ab3f1b'::uuid,'40b35a5f-c3d7-412a-a3fe-4f6452bc72ad'::uuid);
INSERT INTO inventory(id, book_id) VALUES('27833b8c-e777-4efe-b79b-80fa069db17d'::uuid,'5f6ac41d-736e-48ea-893a-8b5f9b138e67'::uuid);
INSERT INTO inventory(id, book_id) VALUES('392ca1dd-5b8b-44bb-a20c-9f21ee747a03'::uuid,'5f6ac41d-736e-48ea-893a-8b5f9b138e67'::uuid);
INSERT INTO inventory(id, book_id) VALUES('eb7fe31c-f007-4bbe-8dca-1543aa20657d'::uuid,'5f6ac41d-736e-48ea-893a-8b5f9b138e67'::uuid);
INSERT INTO inventory(id, book_id) VALUES('42d26686-8264-4097-81c9-cbcdd25c3474'::uuid,'625e265c-30fb-4dc8-98a6-c5e1734a4e08'::uuid);
INSERT INTO inventory(id, book_id) VALUES('554fe4ad-e3cb-4eb5-8012-b695c51dedee'::uuid,'625e265c-30fb-4dc8-98a6-c5e1734a4e08'::uuid);
INSERT INTO inventory(id, book_id) VALUES('777ca193-511e-4025-85e4-7e702df566a2'::uuid,'625e265c-30fb-4dc8-98a6-c5e1734a4e08'::uuid);
INSERT INTO inventory(id, book_id) VALUES('f1eb31b6-545b-480e-8bb6-6dba079b7140'::uuid,'703bcc77-2b38-422e-90e6-fd60e7ea8743'::uuid);
INSERT INTO inventory(id, book_id) VALUES('2a3ff7ed-9b8d-430e-b783-6ff4cf5380bb'::uuid,'703bcc77-2b38-422e-90e6-fd60e7ea8743'::uuid);
INSERT INTO inventory(id, book_id) VALUES('dd95d1e8-7cd1-4a2d-93af-81882f4aca52'::uuid,'703bcc77-2b38-422e-90e6-fd60e7ea8743'::uuid);
INSERT INTO inventory(id, book_id) VALUES('7f44d695-4093-4f7e-b42c-e3a6e6f15bc4'::uuid,'134c2961-8a6a-460e-befa-0ca975005e2c'::uuid);
INSERT INTO inventory(id, book_id) VALUES('df7b39c9-d943-47ab-9b76-0e464dfafc4d'::uuid,'134c2961-8a6a-460e-befa-0ca975005e2c'::uuid);
INSERT INTO inventory(id, book_id) VALUES('4a06314a-205e-4efb-be11-196f2e4fc9cf'::uuid,'134c2961-8a6a-460e-befa-0ca975005e2c'::uuid);
INSERT INTO inventory(id, book_id) VALUES('9430b459-8178-4137-aa00-7452ee966e16'::uuid,'1b1248ca-14e0-4dc9-a0e7-e1eabdf02c36'::uuid);
INSERT INTO inventory(id, book_id) VALUES('6ca5944f-8196-416e-bdb7-cc8e7b26f5d2'::uuid,'1b1248ca-14e0-4dc9-a0e7-e1eabdf02c36'::uuid);
INSERT INTO inventory(id, book_id) VALUES('4d12e01f-87bb-44b6-9f29-7ea7bda6ebd0'::uuid,'1b1248ca-14e0-4dc9-a0e7-e1eabdf02c36'::uuid);
