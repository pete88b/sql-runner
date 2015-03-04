--sqlrunner.name: create-table-a
create table a as
select rownum as number_col, 'a' as text_col
  from all_objects
 where rownum <= 20;

--sqlrunner.name: select-from-table-a
select * from a where number_col < ?;

drop table a;