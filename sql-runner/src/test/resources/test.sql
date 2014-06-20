/*
  SQL script for unit tests.
*/

-- not a sql runner comment
update a
   set b = 2
 where 1 = 2;

--sqlrunner: not-the-real-statement-name

/*
any old comment
*/
--sqlrunner: query1
/* any old comment2 */
SELECT *
  FROM dual;

--sqlrunner:query2
SELECT *
  FROM dual;

/*
ignore me
*/

-- ignore me too