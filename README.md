This was a Java and MySQL project for a back end development boot camp which I participated in between November, 2024 - April, 2025

There is a SQL database which allows the user to enter data related to DIY projects.  The database has 5 tables for project, category, a join table of project_category, material and steps.  
The project table has columns for project_id, name, estimated and actual hours, difficulty and notes.
The category table has columns of category_id and category name.
The project_category table has columns for project_id and category_id.  It is a join table since there can be many projects, each with multiple categories, and there can be many categories, each containing several projects (making this a many-to-many relationship).
The material table has columns for material_id, project_id, material_name, num_required and cost.
The step table has columns for step_id, project_id, step_text and step_order.

When the java program is running, a menu is displayed which allows the user to:
1) Add a project
2) List all projects
3) Select a project
4) Update project details
5) Delete a project
6) Add materials to a project
7) Add a new step to a project
8) Rearrange the step order

Of these, items 1-5 were required for the assignment in the boot camp course.  I added items 6-8 on my own.  Using items 1-5, all of the CRUD operations (Create, Read, Update and Delete) were demonstrated.  Items 6-8 used just Read and Update operations for the SQL database.

This was a great project to learn about the MySQL functions and to expand my Java programming exposure.
