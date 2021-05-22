CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`email`)
);

CREATE TABLE `tb_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `authority` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

CREATE TABLE `tb_user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`),
  FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`)
) ;

CREATE TABLE `tb_customers` (
 `id` bigint NOT NULL AUTO_INCREMENT,
 `name` varchar(255) DEFAULT NULL,
 `birth_date` datetime,
 PRIMARY KEY (`id`)
) ;

INSERT INTO tb_user (first_name, last_name, email, password) VALUES ('Alex', 'Brown', 'alex@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG');
INSERT INTO tb_user (first_name, last_name, email, password) VALUES ('Maria', 'Green', 'maria@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG');

INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);


INSERT INTO tb_customers (name, birth_date) VALUES ('1-Customers', now());
INSERT INTO tb_customers (name, birth_date) VALUES ('2-Customers', now());
INSERT INTO tb_customers (name, birth_date) VALUES ('3-Customers', now());
INSERT INTO tb_customers (name, birth_date) VALUES ('4-Customers', now());
INSERT INTO tb_customers (name, birth_date) VALUES ('5-Customers', now());