#
# This table is an example of creation of events table
# the size of the columns are orietnative and can be 
# replaced with any other.
#
CREATE TABLE events ( 
minute VARCHAR(64) DEFAULT '*', 
hour VARCHAR(64) DEFAULT '*',
dayOfMonth VARCHAR(64) DEFAULT '*',
month VARCHAR(64) DEFAULT '*', 
dayOfWeek VARCHAR(64) DEFAULT '*', 
task VARCHAR(255),
extraInfo VARCHAR(255),
INDEX task(task(64))
);
# 
# This is other option this is a binary format and what is 
# stored in the blod is the CrontabentryBean. This only works 
# in mysql
#
CREATE TABLE crontask(
id integer,
CrontabentryBean BLOB);
#  This the same but for postgresql
#CREATE TABLE crontask(
#id integer,
#CrontabentryBean OID);
