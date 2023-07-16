package com.infy.infyinterns.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.service.ProjectAllocationService;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/infyinterns")
@Validated
public class ProjectAllocationAPI
{

    // add new project along with mentor details
	@Autowired
	Environment environment;
	@Autowired
	ProjectAllocationService projectService;
	
	@PostMapping(value = "/project")
    public ResponseEntity<String> allocateProject( @Valid @RequestBody ProjectDTO project) throws InfyInternException
    {
    	
	Integer projId = 	projectService.allocateProject(project);
	String successMsg = environment.getProperty("API.ALLOCATION_SUCCESS") + projId;
	
	return new ResponseEntity<>(successMsg,HttpStatus.CREATED);
    }

    // get mentors based on idea owner
	@GetMapping(value = "/mentor/{numberOfProjectsMentored}")
    public ResponseEntity<List<MentorDTO>> getMentors(@PathVariable Integer numberOfProjectsMentored) throws InfyInternException
    {
	List<MentorDTO> mentor = 	projectService.getMentors(numberOfProjectsMentored);
	return new ResponseEntity<>(mentor,HttpStatus.OK);
    }

    // update the mentor of a project
	@PutMapping(value = "/project/{ projectId }/{ mentorId }")
    public ResponseEntity<String> updateProjectMentor(@PathVariable Integer projectId,
						     @Valid @PathVariable Integer mentorId) throws InfyInternException
    {
     projectService.updateProjectMentor(projectId, mentorId);
     String successMsg = environment.getProperty("API.PROJECT_UPDATE_SUCCESS");
	return new ResponseEntity<>(successMsg, HttpStatus.OK);
    }

    // delete a project
	@DeleteMapping(value="project/{ projectId }")
    public ResponseEntity<String> deleteProject(Integer projectId) throws InfyInternException
    {
		projectService.deleteProject(projectId);
		String msg = environment.getProperty("API.PROJECT_DELETE _SUCCESS");
		
	return new ResponseEntity<>(msg,HttpStatus.OK);
    }

}
