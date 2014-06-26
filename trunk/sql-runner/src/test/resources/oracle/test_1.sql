create table a as
select rownum as number_col, 'a' as text_col
  from all_objects
 where rownum < 2;

select * from a;

drop table a;