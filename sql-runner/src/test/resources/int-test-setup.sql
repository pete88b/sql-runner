create table study(
  study_id integer constraint study_pk primary key,
  study_name varchar2(200)
);

create sequence study_id;

create or replace
trigger bi_fer_study
before insert on study
for each row
begin
  if (:new.study_id is null)
  then
    select study_id.nextval
      into :new.study_id
      from dual;
  end if;
end;