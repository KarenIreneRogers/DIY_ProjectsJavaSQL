package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;

public class ProjectService {

	private ProjectDao projectDao = new ProjectDao();

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	public List<Project> fetchAllProjects() {

		return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(
				() -> new NoSuchElementException("Project with project ID=" + projectId + " does not eist."));

	}

	public void modifyProjectDetails(Project project) {
		if (!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		}

	}

	public void deleteProject(Integer projectToDelete) {
		if(!projectDao.deleteProject(projectToDelete)) {
			throw new DbException("Project with ID=" + projectToDelete + "does not exist.");
			
		}
		
	}

	public void addMaterial(Material material) {
		if(!projectDao.addNewMaterial(material)) {
			throw new DbException("Material with ID=" + material.getMaterialId() + " does not exist.");
		}
		
	}

	public void addNewStep(Step step) {
		if(!projectDao.addNewStep(step)) {
			throw new DbException("Step with ID=" + step.getStepId() + " does not exist.") ;
		}
		
	}

	public void reorderSteps(List<Step> steps, List<Integer> newStepIdOrder) {
		if(!projectDao.reorderTheSteps(steps, newStepIdOrder)) {
			throw new DbException("New step order does not exist");
			
		}
		
	}

}
