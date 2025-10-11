#
#
# then uncomment then set database name and username to create you need databases
#
# example: .env MYSQL_USER=appuser and needed db name is myshop_db
#
#    CREATE DATABASE IF NOT EXISTS `myshop_db` ;
#    GRANT ALL ON `myshop_db`.* TO 'appuser'@'%' ;
#
#
# this sql script will auto run when the mysql container starts and the $DATA_PATH_HOST/mysql not found.
#
# if your $DATA_PATH_HOST/mysql exists and you do not want to delete it, you can run by manual execution:
#
#     docker-compose exec mysql bash
#     mysql -u root -p < /docker-entrypoint-initdb.d/createdb.sql
#

CREATE DATABASE IF NOT EXISTS `user` COLLATE 'utf8mb4_general_ci' ;
GRANT ALL ON `user`.* TO 'root'@'%' ;

SET GLOBAL log_bin_trust_function_creators = 1;

CREATE USER 'user_read'@'%' IDENTIFIED BY 'enter_2106';
GRANT SELECT ON user.* TO 'user_read'@'%';

CREATE USER 'user_write'@'%' IDENTIFIED BY 'enter_2106';
GRANT LOCK TABLES, ALTER, CREATE, CREATE VIEW, TRIGGER, DROP, REFERENCES, INDEX, INSERT, UPDATE, SELECT, DELETE ON user.* TO 'user_write'@'%';

CREATE DATABASE IF NOT EXISTS `audit` COLLATE 'utf8_general_ci' ;
GRANT ALL ON `audit`.* TO 'root'@'%' ;

SET GLOBAL log_bin_trust_function_creators = 1;

CREATE USER 'audit_read'@'%' IDENTIFIED BY 'enter_2106';
GRANT SELECT ON audit.* TO 'audit_read'@'%';

CREATE USER 'audit_write'@'%' IDENTIFIED BY 'enter_2106';
GRANT LOCK TABLES, ALTER, CREATE, CREATE VIEW, TRIGGER, DROP, REFERENCES, INDEX, INSERT, UPDATE, SELECT, DELETE ON audit.* TO 'audit_write'@'%';

CREATE DATABASE IF NOT EXISTS `product` COLLATE 'utf8mb4_general_ci' ;
GRANT ALL ON `product`.* TO 'root'@'%' ;

SET GLOBAL log_bin_trust_function_creators = 1;

CREATE USER 'product_read'@'%' IDENTIFIED BY 'enter_2106';
GRANT SELECT ON product.* TO 'product_read'@'%';

CREATE USER 'product_write'@'%' IDENTIFIED BY 'enter_2106';
GRANT LOCK TABLES, ALTER, CREATE, CREATE VIEW, TRIGGER, DROP, REFERENCES, INDEX, INSERT, UPDATE, SELECT, DELETE ON product.* TO 'product_write'@'%';

CREATE DATABASE IF NOT EXISTS `money` COLLATE 'utf8mb4_general_ci' ;
GRANT ALL ON `money`.* TO 'root'@'%' ;

SET GLOBAL log_bin_trust_function_creators = 1;

CREATE USER 'money_read'@'%' IDENTIFIED BY 'enter_2106';
GRANT SELECT ON money.* TO 'money_read'@'%';

CREATE USER 'money_write'@'%' IDENTIFIED BY 'enter_2106';
GRANT LOCK TABLES, ALTER, CREATE, CREATE VIEW, TRIGGER, DROP, REFERENCES, INDEX, INSERT, UPDATE, SELECT, DELETE ON money.* TO 'money_write'@'%';

CREATE DATABASE IF NOT EXISTS `dashboard` COLLATE 'utf8mb4_general_ci' ;
GRANT ALL ON `dashboard`.* TO 'root'@'%' ;

SET GLOBAL log_bin_trust_function_creators = 1;

CREATE USER 'dashboard_read'@'%' IDENTIFIED BY 'enter_2106';
GRANT SELECT ON dashboard.* TO 'dashboard_read'@'%';

CREATE USER 'dashboard_write'@'%' IDENTIFIED BY 'enter_2106';
GRANT LOCK TABLES, ALTER, CREATE, CREATE VIEW, TRIGGER, DROP, REFERENCES, INDEX, INSERT, UPDATE, SELECT, DELETE ON dashboard.* TO 'dashboard_write'@'%';

FLUSH PRIVILEGES ;

