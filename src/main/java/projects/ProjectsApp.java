package projects;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	
	private Project curProject;
	
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project",
			"6) Add materials to a project",
			"7) Add a new step to a project",
			"8) Rearrange the step order"
	);
	// @formatter:on

	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();

//   ******** Main method  *********** //
	public static void main(String[] args) {

		new ProjectsApp().processUserSelections();
	}

	private void processUserSelections() {
		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();
				switch (selection) {

				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				case 4:
					updateProjectDetails();
					break;
				case 5:
					deleteProject();
					break;
				case 6:
					addAMaterialToProject();
					break;
				case 7:
					addANewStepToProject();
					break;
				case 8:
					rearrangeStepOrder();
					break;
					
				default:
					System.out.println("\n" + selection + " is not a valid selection.  Try again");
					break;

				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}

	}

	private void rearrangeStepOrder() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		
		List<Step> steps = curProject.getSteps();
		List<Step> stepsInOrder = steps.stream().sorted((r1, r2) -> r1.getStepOrder() - r2.getStepOrder()).collect(Collectors.toList());
		
		List<Integer> stepIds = new LinkedList<Integer>();
		
		// Display steps in current order
		System.out.println("\nCurrent order of steps:");
		for (Step step : stepsInOrder) {
			System.out.println("  Step_ID: " + step.getStepId() + "   " + step.getStepText()) ;
			stepIds.add(step.getStepId()) ;	
		}
		List<Integer> newStepIdOrder = new LinkedList<Integer>();
		Integer tempNewStep = null;
		
		System.out.println("\nEnter the new order of the steps using the Step_ID.");
		
		for (int i=1; i<=stepsInOrder.size(); i++) {
			tempNewStep = getIntInput("Enter Step_ID of step "+ i );
			if(! stepIds.contains(tempNewStep) ) {
				System.out.println("Invalid Step_ID.  Please re-enter");
				i--;
			}else {
				newStepIdOrder.add(tempNewStep);
			}
		}
		
		// Check order entered.  Need to check for completeness and valid step numbers for this project
		System.out.println(newStepIdOrder);
		projectService.reorderSteps(steps,newStepIdOrder);
		
		// Update the curProject object with new step order
		Integer projectId = curProject.getProjectId();
		curProject = null;
		curProject = projectService.fetchProjectById(projectId);	
		
		
	}
	

	private void addANewStepToProject() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		
		List<Step> steps = curProject.getSteps();
		
		System.out.println("\nThe current steps are:");
		for (Step step: steps) {
			System.out.println("   " + step.getStepOrder() + ")  " + step.getStepText() );
		}
		
		String newStepText = getStringInput("Enter the text of the new step") ;
		
		Integer newStepOrder = getIntInput("Enter the order of the new step");
		
		for(Step step: steps) {
			if(newStepOrder == step.getStepOrder()) {
				System.out.println("\nThat step order has already been used.  New step not added.");
				return;
			}
		}
		
		Step step = new Step();
		
		step.setProjectId(curProject.getProjectId());
		step.setStepText(newStepText);
		step.setStepOrder(newStepOrder);
		
		projectService.addNewStep(step);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
		
	}

	private void addAMaterialToProject() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		
		Integer projectId = curProject.getProjectId();
		String materialName = getStringInput("Enter the material name");
		Integer numRequired = getIntInput("Enter the number required");
		BigDecimal cost = getDecimalInput("Enter the cost");
		
		Material material = new Material();
		material.setProjectId(projectId);
		material.setMaterialName(materialName);
		material.setNumRequired(numRequired);
		material.setCost(cost);
		
		projectService.addMaterial(material);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	private void deleteProject() {
		listProjects();
		Integer projectToDelete = getIntInput("Enter the project number to delete");
		projectService.deleteProject(projectToDelete);
		
		System.out.println("\n Project " + projectToDelete + " has been deleted.");
		if(Objects.nonNull(curProject)) {
			if(projectToDelete == curProject.getProjectId()) {
			
			curProject = null;
			/*
			 * curProject.setProjectId(null); curProject.setActualHours(null);
			 * curProject.setEstimatedHours(null); curProject.setDifficulty(null);
			 * curProject.setProjectName(null); curProject.setNotes(null);
			 */		
			}
		}
		
	}

	private void updateProjectDetails() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		  
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		Integer difficulty = getIntInput("Enter the difficulty level [" + curProject.getDifficulty() + "]" );
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		
		Project project = new Project();
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		project.setProjectId(curProject.getProjectId());
		
		
		projectService.modifyProjectDetails(project);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId() );
		
	}

	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project: ");
		
		/* Unselect the current project */
		curProject = null;
		
		/* This will throw an exception if an invalid project ID is entered */
		curProject = projectService.fetchProjectById(projectId);		
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects");
		projects.forEach(
				project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
	}

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}

	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	}

	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");

		return Objects.isNull(input) ? -1 : input;
	}

	private void printOperations() {
		System.out.println("\nThese are the available selections.  Press the Enter key to quit:");
		operations.forEach(line -> System.out.println("   " + line));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		}
		else {
			

			System.out.println("\nYou are working with project: " + curProject);
		}
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;

		}
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();

		return input.isBlank() ? null : input.trim();
	}

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;

		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}

	}

}
