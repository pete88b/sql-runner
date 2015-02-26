--sqlrunner.failfast: false
DROP TABLE study;

--sqlrunner.failfast: true
CREATE TABLE study (
  STUDY_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT STUDY_PK PRIMARY KEY,
  study_name VARCHAR(200));