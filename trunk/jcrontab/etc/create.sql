#
# This table is an example of creation of events table
# the size of the columns are orietnative and can be 
# replaced with any other.
#
# drop TABLE events;
CREATE TABLE events (
id INTEGER NOT NULL PRIMARY KEY,
second VARCHAR(64) DEFAULT '0',
minute VARCHAR(64) DEFAULT '*', 
hour VARCHAR(64) DEFAULT '*',
dayofmonth VARCHAR(64) DEFAULT '*',
month VARCHAR(64) DEFAULT '*', 
dayofweek VARCHAR(64) DEFAULT '*',
year VARCHAR(64) DEFAULT '*',
task VARCHAR(255),
extrainfo VARCHAR(255),
businessDays VARCHAR(6) DEFAULT 'true'
);
# This update sets correctly the database if you have and older version of Jcrontab
# ALTER TABLE events ADD second VARCHAR(64) DEFAULT '0', year VARCHAR(64) DEFAULT '*';
# 
# This is other option this is a binary format and what is 
# stored in the blob is the CrontabentryBean. This only works 
# in mysql
#
#CREATE TABLE crontask(
#id integer,
#crontabentrybean BLOB);
#  This the same but for postgresql
#CREATE TABLE crontask(
#id integer,
#crontabentrybean OID);
# This is the minimum insert to test the functionality some day will include
# all the available tests
# insert into events (second, minute, task, extrainfo) values ('6', '*/2', 'org.jcrontab.tests.TaskTest2', 'each two minutes in second 6')
# insert into events (second, minute, task, extrainfo) values ('15', '*/5', 'org.jcrontab.tests.TaskTest2', 'each five minutes in second 15');
# insert into events values (0, '0', '*', '*', '*', '*', '*', '*', 'org.jcrontab.tests.TastTest2', 'Hola mundo', 'true');
# select * from events
#
# This table is an example of creation of events table
# the size of the columns are orietnative and can be 
# replaced with any other.
#
# drop TABLE processes;
CREATE TABLE processes (
id INTEGER NOT NULL PRIMARY KEY,
second VARCHAR(64) DEFAULT '0',
minute VARCHAR(64) DEFAULT '*', 
hour VARCHAR(64) DEFAULT '*',
dayofmonth VARCHAR(64) DEFAULT '*',
month VARCHAR(64) DEFAULT '*', 
dayofweek VARCHAR(64) DEFAULT '*',
year VARCHAR(64) DEFAULT '*',
task VARCHAR(255),
extrainfo VARCHAR(255),
businessDays VARCHAR(6) DEFAULT 'true'
);
