package com.founderlink.team.service;

import com.founderlink.team.dto.TeamRequest;
import com.founderlink.team.dto.TeamResponse;
import com.founderlink.team.entity.TeamMember;
import com.founderlink.team.event.EventPublisher;
import com.founderlink.team.exception.InvalidInputException;
import com.founderlink.team.exception.ResourceNotFoundException;
import com.founderlink.team.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private EventPublisher eventPublisher;

    // Invite co-founder
    public TeamResponse inviteMember(TeamRequest request) {

        logger.info("Inviting member for startupId: {}", request.getStartupId());

        // Validate startupId
        if (request.getStartupId() == null) {
            throw new InvalidInputException("Startup ID cannot be null!");
        }

        // Validate userId
        if (request.getUserId() == null) {
            throw new InvalidInputException("User ID cannot be null!");
        }

        // Validate role
        if (request.getRole() == null || request.getRole().isEmpty()) {
            throw new InvalidInputException("Role cannot be empty!");
        }

        TeamMember member = new TeamMember();
        member.setStartupId(request.getStartupId());
        member.setUserId(request.getUserId());
        member.setRole(request.getRole());
        member.setStatus("PENDING");

        TeamMember saved = teamRepository.save(member);

     // Publish event to RabbitMQ
     eventPublisher.publishTeamInviteSent(
         saved.getId(),
         saved.getStartupId(),
         saved.getUserId(),
         saved.getRole()
     );

     logger.info("Team member invited with id: {}", saved.getId());
     return mapToResponse(saved);
    }

    // Accept invitation
    public TeamResponse acceptInvitation(Long id) {
        logger.info("Accepting invitation with id: {}", id);
        TeamMember member = teamRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Invitation not found with id: {}", id);
                    return new ResourceNotFoundException("Invitation not found with id: " + id);
                });
        member.setStatus("ACCEPTED");
        TeamMember updated = teamRepository.save(member);
        logger.info("Invitation accepted with id: {}", id);
        return mapToResponse(updated);
    }

    // Get team by startup
    public List<TeamResponse> getTeamByStartup(Long startupId) {
        logger.info("Fetching team for startupId: {}", startupId);
        return teamRepository.findByStartupId(startupId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get teams by user
    public List<TeamResponse> getTeamByUser(Long userId) {
        logger.info("Fetching teams for userId: {}", userId);
        return teamRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get all members
    public List<TeamResponse> getAllMembers() {
        logger.info("Fetching all team members");
        return teamRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper method
    private TeamResponse mapToResponse(TeamMember member) {
        return new TeamResponse(
                member.getId(),
                member.getStartupId(),
                member.getUserId(),
                member.getRole(),
                member.getStatus()
        );
    }
}