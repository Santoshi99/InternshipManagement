package com.infy.infyinterns.service;

import java.util.List;
import java.util.Optional;
import java.util.LinkedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.entity.Mentor;
import com.infy.infyinterns.entity.Project;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.repository.ProjectRepository;

@Transactional
@Service(value = "projectService")
public class ProjectAllocationServiceImpl implements ProjectAllocationService {

	@Autowired
	MentorRepository mentorRepository;
	
	@Autowired
	ProjectRepository projectRepository;
	
	
	@Override
	public Integer allocateProject(ProjectDTO project) throws InfyInternException {

		
			Optional<Mentor> optional = mentorRepository.findById(project.getMentorDTO().getMentorId());
		    optional.orElseThrow(()-> new InfyInternException("Service.MENTOR_NOT_FOUND"));
		    Project projectObj = new Project();
		    Project p = new Project();
		   if(optional.get().getNumberOfProjectsMentored()<3) {
			   projectObj.setIdeaOwner(project.getIdeaOwner());
//			   projectObj.setProjectId(project.getProjectId());
			   projectObj.setProjectName(project.getProjectName());
			   projectObj.setReleaseDate(project.getReleaseDate());
			   projectObj.setMentor(optional.get());
			   
			   p = projectRepository.save(projectObj);
			   updateProjectMentor(p.getProjectId(),p.getMentor().getMentorId());
		   }
		   else
		   {
			   throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		   }
		   return p.getProjectId();
	}

	
	@Override
	public List<MentorDTO> getMentors(Integer numberOfProjectsMentored) throws InfyInternException {
		
		List<Mentor> mentorDet = mentorRepository.findByNumberOfProjectsMentoredGreaterThanEqual(numberOfProjectsMentored);
		MentorDTO mentor = new MentorDTO();
		List<MentorDTO> mDet = new LinkedList<>();
		if(mentorDet.isEmpty()) {
			throw new InfyInternException("Service.MENTOR_NOT_FOUND");
		}
		else {
			for(Mentor m: mentorDet) {
				mentor.setMentorId(m.getMentorId());
				mentor.setMentorName(m.getMentorName());
				mentor.setNumberOfProjectsMentored(m.getNumberOfProjectsMentored());
				mDet.add(mentor);
			}
		}
		return mDet;
	}


	@Override
	public void updateProjectMentor(Integer projectId, Integer mentorId) throws InfyInternException {
		
		Optional<Mentor> optional = mentorRepository.findById(mentorId);
		Mentor mentor= optional.orElseThrow(()-> new InfyInternException("Service.MENTOR_NOT_FOUND"));
		
		if(optional.get().getNumberOfProjectsMentored()>=3) {
			throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		}
		
		Optional<Project> optional2 = projectRepository.findById(projectId);
		optional2.orElseThrow(()-> new InfyInternException("Service.PROJECT_NOT_FOUND"));
		mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()+1);
		
	}

	@Override
	public void deleteProject(Integer projectId) throws InfyInternException {
		
		Optional<Project> optional = projectRepository.findById(projectId);
		Project project = optional.orElseThrow(()->  new InfyInternException("Service.PROJECT_NOT_FOUND"));
		if(project.getMentor()==null) {
			projectRepository.deleteById(projectId);
		}
		else {
			Integer mentorId = project.getMentor().getMentorId();
			Optional<Mentor> op =  mentorRepository.findById(mentorId);
			op.get().setNumberOfProjectsMentored(op.get().getNumberOfProjectsMentored()-1);
			projectRepository.deleteById(projectId);
		}
		
	}
}