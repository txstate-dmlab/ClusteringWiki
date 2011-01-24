/**
 *  ClusteringWiki - personalized and collaborative clustering of search results
 *  Copyright (C) 2010  Texas State University-San Marcos
 *  
 *  Contact: http://dmlab.cs.txstate.edu
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `clustering_wiki`
--

-- --------------------------------------------------------

  
--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `email` varchar(80) NOT NULL,
  `first_name` varchar(30) DEFAULT NULL,
  `last_name` varchar(30) DEFAULT NULL,
  `password` varchar(32) NOT NULL,
  `last_login` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;


--
-- Add default test user
-- 

INSERT INTO `users` ( `id` , `email` , `first_name` , `last_name` , `password` , `last_login` )
VALUES ( NULL , 'all', 'All', 'Users', '', NOW( ) );

INSERT INTO `users` ( `id` , `email` , `first_name` , `last_name` , `password` , `last_login` )
VALUES ( NULL , 'testcw@cs.txstate.edu', 'Test', 'User', 'D41D8CD98F00B204E9800998ECF8427E', NOW( ) );

INSERT INTO `users` ( `id` , `email` , `first_name` , `last_name` , `password` , `last_login` )
VALUES ( NULL , 'admincw@cs.txstate.edu', 'Admin', 'User', '21232F297A57A5A743894A0E4A801FC3', NOW( ) );

--
-- Table structure for table `credentials_requests`
--

CREATE TABLE IF NOT EXISTS `credentials_requests` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `email` varchar(80) NOT NULL,
  `request_key` varchar(32) NOT NULL,
  `request_time` datetime NOT NULL,
  `valid` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `request_key` (`request_key`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Constraints for table `credentials_requests`
--
ALTER TABLE `credentials_requests`
  ADD CONSTRAINT `FK_CREDENTIALS_REQUESTS_EMAIL` FOREIGN KEY (`email`) 
  	REFERENCES `users` (`email`) on delete cascade;
  	
  	
--
-- Table structure for table `queries`
--

CREATE TABLE IF NOT EXISTS `queries` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(11) UNSIGNED NOT NULL,
  `executed_on` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
  	NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `service` varchar(10) NOT NULL,
  `num_results` int(11) UNSIGNED NOT NULL,
  `query_text` varchar(1000) NOT NULL,
  `parsed_query_text` varchar(1000) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;


--
-- Constraints for table `queries`
--
ALTER TABLE `queries`
  ADD CONSTRAINT `FK_QUERIES_USER_ID` FOREIGN KEY (`user_id`) 
  	REFERENCES `users` (`id`) on delete cascade;

--
-- search table for query text
--
CREATE TABLE `query_search` (
	`query_id` INT( 11 ) NOT NULL ,
	`query_text` VARCHAR( 1000 ) NOT NULL ,
	FULLTEXT ( `query_text` )
) ENGINE = MYISAM;

--
-- Table structure for table `query_responses`
--

CREATE TABLE IF NOT EXISTS `query_responses` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `query_id` int(11) UNSIGNED NOT NULL,
  `url` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;


--
-- Constraints for table `query_responses`
--
ALTER TABLE `query_responses`
  ADD CONSTRAINT `FK_QUERY_RESPONSES_QUERY_ID` FOREIGN KEY (`query_id`) 
  	REFERENCES `queries` (`id`) on delete cascade,
  ADD INDEX ( `url` );
  

--
-- Table structure for table `cluster_edits`
--

CREATE TABLE IF NOT EXISTS `cluster_edits` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `query_id` int(11) UNSIGNED NOT NULL,
  `clustering_algo` int(11) UNSIGNED NOT NULL,
  `path1` varchar(300) NOT NULL,
  `path2` varchar(300) NULL,
  `path3` varchar(300) NULL,
  `path4` varchar(300) NULL,
  `path5` varchar(300) NULL,
  `cardinality` int(11) NOT NULL,
  `executed_on` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
  	NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Constraints for table `cluster_edits`
--
ALTER TABLE `cluster_edits`
  ADD CONSTRAINT `FK_CLUSTER_EDITS_QUERY_ID` FOREIGN KEY (`query_id`) 
  	REFERENCES `queries` (`id`) on delete cascade;
  	

--
-- Table structure for table `cluster_edits_all`
--

CREATE TABLE IF NOT EXISTS `cluster_edits_all` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `query_id` int(11) UNSIGNED NOT NULL,
  `clustering_algo` int(11) UNSIGNED NOT NULL,
  `path1` varchar(300) NOT NULL,
  `path2` varchar(300) NULL,
  `path3` varchar(300) NULL,
  `path4` varchar(300) NULL,
  `path5` varchar(300) NULL,
  `cardinality` int(11) NOT NULL,
  `executed_on` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
  	NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Constraints for table `cluster_edits_all`
--
ALTER TABLE `cluster_edits_all`
  ADD CONSTRAINT `FK_CLUSTER_EDITS_ALL_QUERY_ID` FOREIGN KEY (`query_id`) 
  	REFERENCES `queries` (`id`) on delete cascade;
  	
--
-- Triggers
--

delimiter |

DROP TRIGGER IF EXISTS `query_insert_trg` |
CREATE TRIGGER `query_insert_trg` AFTER INSERT ON `queries`
  FOR EACH ROW 
  BEGIN
    DECLARE allid int;
    SELECT `id` INTO allid FROM `users` WHERE `email` = 'all';
    IF NEW.`user_id` = allid THEN
	    DELETE FROM `query_search` WHERE `query_id` = NEW.`id`;
	    INSERT INTO `query_search` SET `query_id` = NEW.`id`, `query_text` = NEW.`query_text`;
	END IF;
  END;
|


DROP TRIGGER IF EXISTS `query_delete_trg` |
CREATE TRIGGER `query_delete_trg` BEFORE DELETE ON `queries`
  FOR EACH ROW BEGIN
    DELETE FROM `query_search` WHERE `query_id` = OLD.`id`;
  END;
|

DROP TRIGGER IF EXISTS `cluster_edit_insert_trg` |
CREATE TRIGGER `cluster_edit_insert_trg` AFTER INSERT ON `cluster_edits`
  FOR EACH ROW 
  BEGIN
    DECLARE allid int; -- user id for user all
    DECLARE allqid int; -- query id for same query for user all
    DECLARE alleid int; -- cluster edit tupple id for same edit saved for user all
    DECLARE allc int; -- cardinality for the cluster edit for user all
    DECLARE service int;
    DECLARE num_results int;
    DECLARE query_text VARCHAR(1000);
    DECLARE parsed_query_text VARCHAR(1000);
    -- get query id for query saved for user all with same parameters as current cluster edit query
    SELECT `id` INTO allid FROM `users` WHERE `email` = 'all';
    SELECT q1.`id` INTO allqid FROM `queries` q1, `queries` q2 
    	WHERE q1.`user_id` = allid AND q2.`id` = NEW.`query_id` AND 
    	q2.`service` = q1.`service` AND q2.`num_results` = q1.`num_results`
    	AND q2.`query_text` = q1.`query_text`;
    -- If somehow query was not saved for user all, do it here
    IF allqid IS NULL OR allqid < 1 THEN
    	SELECT `service`, `num_results`, `query_text` INTO service, num_results, query_text FROM `queries` 
    	WHERE `id` = NEW.`query_id`;
    	INSERT INTO `queries` (`id`, `user_id`, `executed_on`, `service`, `num_results`, `query_text`, `parsed_query_text`) 
    	VALUES (NULL, allid, CURRENT_TIMESTAMP, service, num_results, query_text, parsed_query_text);
    	SELECT `id` INTO allqid FROM `queries` WHERE
    	`user_id` = allid AND `service` = service AND `num_results` = num_results AND `query_text` = query_text;
    END IF;
    -- get tupple id for same edit by user all
    SELECT `id`, `cardinality` INTO alleid, allc FROM `cluster_edits_all` 
    WHERE `query_id` = allqid AND `clustering_algo` = NEW.`clustering_algo` 
    AND `path1` = NEW.`path1` AND 
	((NEW.`path2` IS NULL AND `path2` IS NULL) OR `path2` = NEW.`path2`) AND
	((NEW.`path3` IS NULL AND `path3` IS NULL) OR `path3` = NEW.`path3`) AND
	((NEW.`path4` IS NULL AND `path4` IS NULL) OR `path4` = NEW.`path4`) AND
	((NEW.`path5` IS NULL AND `path5` IS NULL) OR `path5` = NEW.`path5`);
	-- if edit not yet saved for user all, save now
	IF alleid IS NULL OR alleid < 1 THEN
		INSERT INTO `cluster_edits_all` (
			`id`, `query_id`, `clustering_algo`, `path1`, 
			`path2`, `path3`, `path4`, `path5`, `cardinality`, `executed_on` )
		VALUES (
		NULL , allqid, NEW.`clustering_algo`, NEW.`path1`, 
		NEW.`path2`, NEW.`path3`, NEW.`path4`, NEW.`path5`, NEW.`cardinality`,
		CURRENT_TIMESTAMP );
	ELSE
		SET allc = allc + NEW.`cardinality`;
		IF allc <> 0 THEN
			UPDATE `cluster_edits_all` SET `cardinality` = allc WHERE `id` = alleid;
		ELSE
			DELETE FROM `cluster_edits_all` WHERE `id` = alleid;
		END IF;
	END IF;
	
  END;
|


DROP TRIGGER IF EXISTS `cluster_edit_update_trg` |
CREATE TRIGGER `cluster_edit_update_trg` AFTER UPDATE ON `cluster_edits`
  FOR EACH ROW 
  BEGIN
    DECLARE allid int; -- user id for user all
    DECLARE allqid int; -- query id for same query for user all
    DECLARE alleid int; -- cluster edit tupple id for same edit saved for user all
    DECLARE allc int; -- cardinality for the cluster edit for user all
    DECLARE service int;
    DECLARE num_results int;
    DECLARE query_text VARCHAR(1000);
    DECLARE parsed_query_text VARCHAR(1000);
    -- get query id for query saved for user all with same parameters as current cluster edit query
    SELECT `id` INTO allid FROM `users` WHERE `email` = 'all';
    SELECT q1.`id` INTO allqid FROM `queries` q1, `queries` q2 
    	WHERE q1.`user_id` = allid AND q2.`id` = NEW.`query_id` AND 
    	q2.`service` = q1.`service` AND q2.`num_results` = q1.`num_results`
    	AND q2.`query_text` = q1.`query_text`;
    -- If somehow query was not saved for user all, do it here
    IF allqid IS NULL OR allqid < 1 THEN
    	SELECT `service`, `num_results`, `query_text` INTO service, num_results, query_text FROM `queries` 
    	WHERE `id` = NEW.`query_id`;
    	INSERT INTO `queries` (`id`, `user_id`, `executed_on`, `service`, `num_results`, `query_text`, `parsed_query_text`) 
    	VALUES (NULL, allid, CURRENT_TIMESTAMP, service, num_results, query_text, parsed_query_text);
    	SELECT `id` INTO allqid FROM `queries` WHERE
    	`user_id` = allid AND `service` = service AND `num_results` = num_results AND `query_text` = query_text;
    END IF;
    -- get tupple id for same edit by user all
    SELECT `id`, `cardinality` INTO alleid, allc FROM `cluster_edits_all` 
    WHERE `query_id` = allqid AND `clustering_algo` = NEW.`clustering_algo` 
    AND `path1` = NEW.`path1` AND 
	((NEW.`path2` IS NULL AND `path2` IS NULL) OR `path2` = NEW.`path2`) AND
	((NEW.`path3` IS NULL AND `path3` IS NULL) OR `path3` = NEW.`path3`) AND
	((NEW.`path4` IS NULL AND `path4` IS NULL) OR `path4` = NEW.`path4`) AND
	((NEW.`path5` IS NULL AND `path5` IS NULL) OR `path5` = NEW.`path5`);
	-- if edit not yet saved for user all, save now
	IF alleid IS NULL OR alleid < 1 THEN
		INSERT INTO `cluster_edits_all` (
			`id`, `query_id`, `clustering_algo`, `path1`, 
			`path2`, `path3`, `path4`, `path5`, `cardinality`, `executed_on` )
		VALUES (
		NULL , allqid, NEW.`clustering_algo`, NEW.`path1`, 
		NEW.`path2`, NEW.`path3`, NEW.`path4`, NEW.`path5`, NEW.`cardinality`,
		CURRENT_TIMESTAMP );
	ELSE 
		SET allc = allc + NEW.`cardinality` - OLD.`cardinality`;
		IF allc <> 0 THEN
			UPDATE `cluster_edits_all` SET `cardinality` = allc WHERE `id` = alleid;
		ELSE
			DELETE FROM `cluster_edits_all` WHERE `id` = alleid;
		END IF;
	END IF;
	
  END;
|

DROP TRIGGER IF EXISTS `cluster_edit_delete_trg` |
CREATE TRIGGER `cluster_edit_delete_trg` AFTER DELETE ON `cluster_edits`
  FOR EACH ROW BEGIN
    DECLARE allid int; -- user id for user all
    DECLARE allqid int; -- query id for same query for user all
    DECLARE alleid int; -- cluster edit tupple id for same edit saved for user all
    DECLARE allc int; -- cardinality for the cluster edit for user all
    DECLARE service int;
    DECLARE num_results int;
    DECLARE query_text VARCHAR(1000);
    DECLARE parsed_query_text VARCHAR(1000);
    -- get query id for query saved for user all with same parameters as current cluster edit query
    SELECT `id` INTO allid FROM `users` WHERE `email` = 'all';
    SELECT q1.`id` INTO allqid FROM `queries` q1, `queries` q2 
    	WHERE q1.`user_id` = allid AND q2.`id` = OLD.`query_id` AND 
    	q2.`service` = q1.`service` AND q2.`num_results` = q1.`num_results`
    	AND q2.`query_text` = q1.`query_text`;
    -- If somehow query was deleted for user all, add it here
    IF allqid IS NULL OR allqid < 1 THEN
    	SELECT `service`, `num_results`, `query_text` INTO service, num_results, query_text FROM `queries` 
    	WHERE `id` = OLD.`query_id`;
    	INSERT INTO `queries` (`id`, `user_id`, `executed_on`, `service`, `num_results`, `query_text`, `parsed_query_text`) 
    	VALUES (NULL, allid, CURRENT_TIMESTAMP, service, num_results, query_text, parsed_query_text);
    	SELECT `id` INTO allqid FROM `queries` WHERE
    	`user_id` = allid AND `service` = service AND `num_results` = num_results AND `query_text` = query_text;
    END IF;
    -- get tupple id for same edit by user all
    SELECT `id`, `cardinality` INTO alleid, allc FROM `cluster_edits_all` 
    WHERE `query_id` = allqid AND `clustering_algo` = OLD.`clustering_algo` 
    AND `path1` = OLD.`path1` AND 
	((OLD.`path2` IS NULL AND `path2` IS NULL) OR `path2` = OLD.`path2`) AND
	((OLD.`path3` IS NULL AND `path3` IS NULL) OR `path3` = OLD.`path3`) AND
	((OLD.`path4` IS NULL AND `path4` IS NULL) OR `path4` = OLD.`path4`) AND
	((OLD.`path5` IS NULL AND `path5` IS NULL) OR `path5` = OLD.`path5`);
	-- if edit not yet saved for user all, save now
	IF alleid IS NULL OR alleid < 1 THEN
		INSERT INTO `cluster_edits_all` (
			`id`, `query_id`, `clustering_algo`, `path1`, 
			`path2`, `path3`, `path4`, `path5`, `cardinality`, `executed_on` )
		VALUES (
		NULL , allqid, OLD.`clustering_algo`, OLD.`path1`, 
		OLD.`path2`, OLD.`path3`, OLD.`path4`, OLD.`path5`, (0 - OLD.`cardinality`),
		CURRENT_TIMESTAMP );
	ELSE 
		SET allc = allc - OLD.`cardinality`;
		IF allc <> 0 THEN
			UPDATE `cluster_edits_all` SET `cardinality` = allc WHERE `id` = alleid;
		ELSE
			DELETE FROM `cluster_edits_all` WHERE `id` = alleid;
		END IF;
	END IF;
	
  END;
|



delimiter ;


CREATE TABLE `tests` (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY ,
	`description` VARCHAR( 500 ) NOT NULL 
) ENGINE = InnoDB;



CREATE TABLE `test_topics` (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
	`query` VARCHAR( 1000 ) NOT NULL ,
	`description` VARCHAR( 4000 ) NOT NULL ,
	`narrative` VARCHAR( 4000 ) NOT NULL ,
	PRIMARY KEY ( `id` ) 
) ENGINE = InnoDB;


CREATE TABLE `test_steps` (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
	`topic_id` INT UNSIGNED NOT NULL ,
	`source` VARCHAR( 10 ) NOT NULL ,
	`results` INT UNSIGNED NOT NULL ,
	`algorithm` INT UNSIGNED NOT NULL ,
	`enable_tagging` INT UNSIGNED NOT NULL ,
	`tag_count` INT UNSIGNED NOT NULL ,
	`logged_in` INT UNSIGNED NOT NULL ,
	`disable_editting` INT UNSIGNED NOT NULL ,
	`hide_cluster` INT UNSIGNED NOT NULL ,
	`query_type` VARCHAR( 15 ) NOT NULL ,
	PRIMARY KEY ( `id` ) ,
	FOREIGN KEY ( `topic_id` ) REFERENCES `test_topics` ( `id` )
		ON DELETE CASCADE
) ENGINE = InnoDB;


CREATE TABLE `test_executions` (
	`id` VARCHAR( 10 ) NOT NULL ,
	`test_id` INT UNSIGNED NOT NULL ,
	PRIMARY KEY ( `id` ) ,
	FOREIGN KEY ( `test_id` ) REFERENCES `tests` ( `id` )
		ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE `test_step_executions` (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`execution_id` VARCHAR( 10 ) NOT NULL ,
	`step_id` INT UNSIGNED NOT NULL ,
	`user_effort` DOUBLE UNSIGNED NOT NULL ,
	`base_effort` DOUBLE UNSIGNED NOT NULL ,
	`base_rel_effort` DOUBLE UNSIGNED NOT NULL ,
	`uid` INT UNSIGNED NULL ,
	`cluster` TEXT NOT NULL ,
	`tags` VARCHAR( 10000 ) NOT NULL ,
	`times` VARCHAR( 2000 ) NOT NULL ,
	PRIMARY KEY ( `id` ) ,
	FOREIGN KEY ( `execution_id` ) REFERENCES `test_executions` ( `id` ) 
		ON DELETE CASCADE ,
	FOREIGN KEY ( `step_id` ) REFERENCES `test_steps` ( `id` ) 
		ON DELETE CASCADE 
) ENGINE = InnoDB;

CREATE TABLE `test_details` (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY ,
	`test_id` INT UNSIGNED NOT NULL ,
	`step_id` INT UNSIGNED NOT NULL ,
	`step_order` INT UNSIGNED NOT NULL ,
	FOREIGN KEY ( `test_id` ) REFERENCES `tests` ( `id` )
		ON DELETE CASCADE ,
	FOREIGN KEY ( `step_id` ) REFERENCES `test_steps` ( `id` ) 
		ON DELETE CASCADE 
) ENGINE = InnoDB ;

-- test data
INSERT INTO `tests` (`id`, `description`) VALUES
(1, 'Training track');

INSERT INTO `test_executions` (`id`, `test_id`) VALUES
('train00001', 1);

INSERT INTO `test_topics` (`id`, `query`, `description`, `narrative`) VALUES
(1, '', 'Welcome to the ClusteringWiki user study training. You will be asked to find and tag relevant results for a number of queries, edit a few clusters, and log in and out of the application a few times. Please ensure the queries you tag are really relevant to the query narrative presented.  The quality of this study depends on it.  Whenever you are ready, click ''Next Step'' to get started.', ''),
(2, 'topic:501 Michael Jordan', 'Find relevant pages about Michael Jordan, the basketball player. You can tag a relevant page by clicking the red exclamation icon next to the result.  If you made a mistake, click it again to un-tag it. You can select one of the clusters before tagging a result if you think a relevant result may be in that cluster.', 'Relevant pages will mainly contain information about Michael Jordan, the basketball player, not other people with the same name.'),
(3, 'topic:82 Genetic Engineering', 'Find relevant pages that discuss a genetic engineering application, a product that has been, is being, or will be developed by genetic manipulation, or attitudes toward genetic engineering.', 'A relevant document will discuss a product, e.g., drug, microorganism, vaccine, animal, plant, agricultural product, developed by genetic engineering techniques; identify an application, such as to clean up the environment or human gene therapy for a specific problem; or, present human attitudes toward genetic engineering.'),
(4, 'topic:502 Michael Jordan', 'Edit the resulting query cluster making at least one edit you deem necessary so you may easily find pages referring to games played by Michael Jordan.', 'Relevant pages will mainly contain information about games played by Michael Jordan, not any of his other business endeavors.'),
(5, 'topic:503 Jim Gray', 'Find the Microsoft&reg; News Center page about Jim Gray receiving the Turing Award for his work in the Database field.', 'There is a specific page you are searching for, published on www.microsoft.com/presspass and described above.'),
(6, 'topic:502 Michael Jordan', 'Find pages about games played by Michael Jordan, the basketball player.', 'Relevant pages will mainly contain information about games played by Michael Jordan, not any of his other business endeavors.'),
(7, 'topic:503 Jim Gray', 'Repeat the previous task of finding the Microsoft&reg; News Center page about Jim Gray receiving the Turing Award for his work in the Database field.', 'There is a specific page you are searching for, published on www.microsoft.com/presspass and described above.');


INSERT INTO `test_steps` (`id`, `topic_id`, `source`, `results`, `algorithm`, `enable_tagging`, `tag_count`, `logged_in`, `disable_editting`, `hide_cluster`, `query_type`) VALUES
(1, 1, '', 0, 0, 0, 0, 2, 0, 0, ''),
(2, 2, 'google', 50, 3, 1, 10, 0, 0, 0, 'i'),
(3, 3, 'ap', 50, 3, 1, 5, 1, 1, 0, 'i'),
(4, 4, 'google', 50, 3, 0, 0, 1, 0, 0, 'i'),
(5, 5, 'google', 50, 3, 1, 1, 1, 1, 0, 'n'),
(6, 6, 'google', 50, 3, 1, 5, 0, 0, 0, 'i'),
(7, 7, 'google', 50, 3, 1, 1, 2, 0, 1, 'n');


INSERT INTO `test_details` (`id`, `test_id`, `step_id`, `step_order`) VALUES
(1, 1, 1, 1),
(2, 1, 2, 2),
(3, 1, 3, 3),
(4, 1, 4, 4),
(5, 1, 5, 5),
(6, 1, 6, 6),
(7, 1, 7, 7);




