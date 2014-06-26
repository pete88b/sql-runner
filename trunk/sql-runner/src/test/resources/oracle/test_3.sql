create table a as
select rownum as number_col, chr(rownum + 64) as text_col
  from all_objects
 where rownum < 4;

select * from a;

drop table a;