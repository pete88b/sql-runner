/*
  SQL script for unit tests.
*/

-- not a sql runner comment
update a
   set b = 2
 where 1 = 2;

--sqlrunner.name: not-the-real-statement-name

/*
any old comment
*/
--sqlrunner.name: query1
/* any old comment2 */
SELECT *
  FROM dual;

--sqlrunner.name:query2
SELECT *
  FROM dual;

/*
ignore me
*/

-- ignore me too