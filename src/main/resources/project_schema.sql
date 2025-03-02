DROP TABLE IF EXISTS   project_category  ;
DROP TABLE IF EXISTS   category  ;
DROP TABLE IF EXISTS   step  ;
DROP TABLE IF EXISTS   material  ;
DROP TABLE IF EXISTS   project  ;

CREATE TABLE  project  (   
	project_id INT NOT NULL AUTO_INCREMENT,

	project_name VARCHAR(128) NOT NULL,
	estimated_hours DECIMAL(7,2),
	actual_hours DECIMAL(7,2),
	difficulty INT,
	notes TEXT,
	PRIMARY KEY (project_id) 
);

CREATE TABLE  material (    
	material_id INT NOT NULL  AUTO_INCREMENT ,
	project_id INT NOT NULL,
	material_name VARCHAR(128) NOT NULL,
	num_required INT,
	cost DECIMAL(7,2),
	PRIMARY KEY (material_id),
	FOREIGN KEY(project_id) REFERENCES project(project_id) ON DELETE CASCADE
);

CREATE TABLE step  (    
	step_id INT  NOT NULL AUTO_INCREMENT,
	project_id INT NOT NULL,
	step_text TEXT NOT NULL,
	step_order INT NOT NULL,
	PRIMARY KEY (step_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE  category (    
	category_id INT  NOT NULL   AUTO_INCREMENT ,
	category_name VARCHAR(128) NOT NULL,
	PRIMARY KEY (category_id)
);

CREATE TABLE  project_category (    
	project_id INT NOT NULL,
	category_id INT NOT NULL,
	FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
	FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
	UNIQUE KEY (project_id, category_id)
);

INSERT INTO project(project_name, estimated_hours, actual_hours, difficulty, notes) 
	VALUES('Wash windows', 0.5, 0.75, 1, 'Use clean cloths or paper towels for drying'),
	('Clean downspout',2.5, 2.0, 3,'Get all the leaves out' );
INSERT INTO material (project_id, material_name, num_required) 
	VALUES (1, 'rags',5),(2, 'bucket',1);
INSERT INTO step (project_id, step_text, step_order) 
	VALUES (1, 'Make bucket of soapy water', 1), 
	(1, 'Place ladder near outside surface', 2), 
	(1, 'Use soapy water to clean window with sponge',3);
INSERT INTO step (project_id, step_text, step_order) 
	VALUES (2, 'Place tall ladder', 1), 
	(2, 'Hook up hose with sprayer on end', 2), 
	(2, 'Turn on water and take hose to top of ladder', 3);
INSERT INTO category (category_name) 
	VALUES ('Doors and windows'),('Cleaning'), ('Annual maintenance' );
INSERT INTO project_category (project_id, category_id) 
	VALUES (1, 1), (1,2), (2,3);
