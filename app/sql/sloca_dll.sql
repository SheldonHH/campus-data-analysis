DROP DATABASE IF EXISTS `SLOCA`;

CREATE SCHEMA `SLOCA`;

USE `SLOCA`;

CREATE TABLE `SLOCA`.`DEMOGRAPHICS`(
`mac_address` varchar(40) not null, 
`name` varchar(45) not null, 
`password` varchar(45) not null,
`email` varchar(50) not null, 
`gender` char(1) not null,
primary key (`mac_address`)
);

CREATE  TABLE `sloca`.`location_lookup` (
`location_id` INT NOT NULL ,
`semantic_place` VARCHAR(45) NOT NULL ,
PRIMARY KEY (`location_id`)
);

CREATE  TABLE IF NOT EXISTS `sloca`.`location` (
`rowNumber` INT NOT NULL,
`timestamp` TIMESTAMP NOT NULL ,
`mac_address` VARCHAR(40) NOT NULL ,
`location_id` INT NULL ,
PRIMARY KEY (`rowNumber`)
);
                
ALTER TABLE `sloca`.`location` ADD INDEX (`timestamp`, `mac_address`, `location_id`);