SELECT p.name, c.name
FROM person as p
LEFT JOIN company as c
ON p.company_id = c.id
WHERE p.company_id != 5 OR p.company_id IS NULL;

SELECT c.name, COUNT(p.name)
FROM company as c
JOIN person as p
on c.id = p.company_id
GROUP BY c.name
HAVING COUNT(p.name) = (
	SELECT MAX(person_count)
	FROM (
		SELECT COUNT(company_id) as person_count
		FROM person
		GROUP BY company_id
	)
);