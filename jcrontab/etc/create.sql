#
# This table is an example of creation of events table
# the size of the columns are orietnative and can be 
# replaced with any other.
#
CREATE TABLE EVENTS ( 
MINUTE VARCHAR(64) DEFAULT '*', 
HOUR VARCHAR(64) DEFAULT '*',
DAYOFMONTH VARCHAR(64) DEFAULT '*',
MONTH VARCHAR(64) DEFAULT '*', 
DAYOFWEEK VARCHAR(64) DEFAULT '*', 
TASK VARCHAR(255),
EXTRAINFO VARCHAR(255)
);
# 
# This is other option this is a binary format and what is 
# stored in the blod is the CrontabentryBean. This only works 
# in mysql
#
#CREATE TABLE crontask(
#ID integer,
#CRONTABENTRYBEAN BLOB);
#  This the same but for postgresql
#CREATE TABLE crontask(
#ID integer,
#CRONTABENTRYBEAN OID);
# This is the minimum insert to test the functionality some day will include
# all the available tests
# insert into events (task, extraInfo) values ('org.jcrontab.tests.TaskTest2', 
# 'eachMinute1');
