/* Query 1 */
Spool result1.lst
SELECT L.LID, L.LEAGUE_NAME, T.TEAM_NAME, L.YEAR
FROM LEAGUES L, TEAMS T
WHERE T.TID = L.CHAMPION_TID AND L.SEASON = 'Summer'
ORDER BY L.LID ASC;
Spool Off

/* Query 2 */
Spool result2.lst
CREATE OR REPLACE VIEW tmpCount (REGION_NAME, FOOTBALL_RANKING, NUM) AS 
	SELECT REGION_NAME, FOOTBALL_RANKING, COUNT(*) AS NUM
	FROM LEAGUES L, REGIONS R
	WHERE L.RID = R.RID AND SEASON = 'Spring'
	GROUP BY REGION_NAME, FOOTBALL_RANKING;

SELECT REGION_NAME, FOOTBALL_RANKING 
FROM tmpCount 
WHERE NUM = (
	SELECT MAX(NUM)
	FROM tmpCount
);
DROP VIEW tmpCount;
Spool Off

/* Query 3 */
Spool result3.lst
SELECT DISTINCT(T.TEAM_NAME)
FROM TEAMS T, LEAGUES L, REGIONS R
WHERE L.CHAMPION_TID = T.TID AND L.RID = R.RID AND R.FOOTBALL_RANKING = (
	SELECT MAX(C2.FOOTBALL_RANKING)
	FROM REGIONS C2
)
ORDER BY T.TEAM_NAME ASC;
Spool Off

/* Query 4 */
Spool result4.lst
SELECT R.REGION_NAME, COUNT(*) AS LEAGUES_NO
FROM REGIONS R, LEAGUES L, TEAMS T
WHERE L.CHAMPION_TID = T.TID AND L.RID = R.RID
GROUP BY R.REGION_NAME
ORDER BY LEAGUES_NO DESC;
Spool Off

/* Query 5 */
Spool result5.lst
SELECT S.SID, S.SPONSOR_NAME, SUM(U.SPONSORSHIP) AS SPONSORSHIP_SUM
FROM SPONSORS S, SUPPORT U, LEAGUES L, REGIONS R
WHERE S.SID = U.SID AND U.LID = L.LID AND L.RID = R.RID AND R.REGION_NAME = 'England'
GROUP BY S.SID, S.SPONSOR_NAME
ORDER BY SPONSORSHIP_SUM DESC;
Spool Off

/* Query 6 */
Spool result6.lst
SELECT L.LID
FROM SPONSORS S, SUPPORT U, LEAGUES L
WHERE S.SID = U.SID AND U.LID = L.LID AND S.MARKET_VALUE >= 30.0
GROUP BY L.LID
HAVING COUNT(DISTINCT S.SID) >= 6 AND SUM(U.SPONSORSHIP) > 2.0
ORDER BY L.LID ASC;
Spool Off

/* Query 7 */
Spool result7.lst
CREATE OR REPLACE VIEW tmpCount (SID, NUM) AS
	SELECT SID, SUM(SPONSORSHIP)
	FROM SUPPORT U, LEAGUES L
	WHERE L.LID = U.LID
	GROUP BY SID;

CREATE OR REPLACE VIEW tmpPercent (SID, RATIO) AS
	SELECT S.SID, T.NUM/S.MARKET_VALUE
	FROM SPONSORS S, tmpCount T
	WHERE S.SID=T.SID;

SELECT S1.SPONSOR_NAME
FROM SPONSORS S1, tmpPercent T
WHERE S1.SID = T.SID AND T.RATIO = (
	SELECT MAX(RATIO)
	FROM tmpPercent
)
ORDER BY S1.SPONSOR_NAME ASC;
DROP VIEW tmpCount;
DROP VIEW tmpPercent;
Spool Off

/* Query 8 */
Spool result8.lst
CREATE OR REPLACE VIEW tmpCount (SID, NUM) AS
	SELECT SID, SUM(SPONSORSHIP)
	FROM SUPPORT
	GROUP BY SID;

CREATE OR REPLACE VIEW tmpCount2 (LID) AS
	SELECT LID
	FROM SUPPORT
	WHERE SID IN (
		SELECT SID
		FROM tmpCount 
		WHERE NUM = (
			SELECT MAX(NUM)
			FROM tmpCount
		)
	);
	
SELECT UNIQUE(L.LID), L.LEAGUE_NAME, L.YEAR, L.SEASON
FROM LEAGUES L
WHERE LID NOT IN (SELECT LID FROM tmpCount2)
ORDER BY L.LID ASC;
DROP VIEW tmpCount;
DROP VIEW tmpCount2;
Spool Off

