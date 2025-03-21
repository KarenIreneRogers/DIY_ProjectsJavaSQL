package projects.dao;

import java.math.BigDecimal;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	public Project insertProject(Project project) {
		// @formatter:off
		String sql = ""
			+ "INSERT INTO " + PROJECT_TABLE + " "
			+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
			+ "VALUES "
			+ "(?, ?, ?, ?, ?);";
		// @formatter:on
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);

				stmt.executeUpdate();

				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);

				project.setProjectId(projectId);
				return project;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public List<Project> fetchAllProjects() {

		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name ";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {

				try (ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();

					while (rs.next()) {
						projects.add(extract(rs, Project.class));
					}
					return projects;
				}
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}

	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try {
				Project project = null;
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);

					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				commitTransaction(conn);

				return Optional.ofNullable(project);
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = ""
				+ "SELECT c.* FROM " + CATEGORY_TABLE + " c "
				+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
				+ "WHERE project_id = ? " ;
		// @formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();
				
				while(rs.next() ) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}
		}
	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT s.* FROM " + STEP_TABLE + " s WHERE project_id = ? ORDER BY step_order ";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>() ;
				
				while(rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT m.* FROM " + MATERIAL_TABLE + " m WHERE project_id = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();
				
				while(rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				return materials;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
		
		// @formatter:off
		String sql = "" 
				+ "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?,"
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?";
		// @formatter:on
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);
				
			
				
				boolean updated = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return updated;
				
			}	catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}	catch(SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectToDelete) {
		String sql = " DELETE FROM project WHERE project_id= ? " ;
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectToDelete, Integer.class);
				
				boolean deleted = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return deleted;
			}
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean addNewMaterial(Material material) {
		String sql = "INSERT INTO " + MATERIAL_TABLE 
				+ "( project_id, material_name, num_required, cost ) " 
				+ "VALUES ( ?, ?, ?, ?) ";
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
		
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, material.getProjectId(), Integer.class);
				setParameter(stmt, 2, material.getMaterialName(), String.class);
				setParameter(stmt, 3, material.getNumRequired(), Integer.class);
				setParameter(stmt, 4, material.getCost(), BigDecimal.class);
				
				boolean updated = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return updated;
				
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}	catch(SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean addNewStep(Step step) {
	
		String sql = "INSERT INTO " + STEP_TABLE 
				+ "(project_id, step_text, step_order ) "
				+ "VALUES (?, ?,  ? )";
		

		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, step.getProjectId(), Integer.class);
				setParameter(stmt, 2, step.getStepText(), String.class);
				setParameter(stmt, 3, step.getStepOrder(), Integer.class) ;
				
				boolean updated = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return updated;
				
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		}	catch(SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean reorderTheSteps(List<Step> steps, List<Integer> newStepIdOrder) {
		String sql = "UPDATE " + STEP_TABLE + " SET step_order = ? WHERE step_id = ? ; ";
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			Integer countUpdated = 0;
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				for(int i=1; i<= steps.size(); i++) {
					setParameter(stmt, 1, i, Integer.class);
					setParameter(stmt, 2, newStepIdOrder.get(i-1), Integer.class);
			
	System.out.println("Here is the stmt:" + stmt+"  " +  countUpdated) ;
					boolean updated = stmt.executeUpdate() == 1;
					if (updated) {
						countUpdated ++;
					}
				}
				
				boolean allGood = (countUpdated == steps.size() );
				commitTransaction(conn);
					
				return allGood;		
			}
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException (e);
			}
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
	}
}

