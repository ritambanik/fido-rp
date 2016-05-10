CREATE TABLE CUSTOMER (
  user_id varchar(20) NOT NULL,
  user_name varchar(100) NOT NULL,
  password varchar(20) NOT NULL,
  device_token blob,
  push_key blob,
  created_on datetime NOT NULL,
  last_updated_on datetime DEFAULT NULL,
  PRIMARY KEY (user_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 /
 
 CREATE TABLE AUTH_REQUEST (
  request_id int(11) NOT NULL,
  user_id varchar(20) NOT NULL,
  requested_time datetime NOT NULL,
  request_type varchar(8) NOT NULL,
  request_status varchar(1) NOT NULL,
  PRIMARY KEY (request_id),
  KEY CUSTOMER_FK_idx (user_id),
  CONSTRAINT `CUSTOMER_FK` FOREIGN KEY (user_id) REFERENCES CUSTOMER (user_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/
