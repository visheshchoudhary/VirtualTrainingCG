package com.founderlink.team;

import com.founderlink.team.dto.TeamRequest;
import com.founderlink.team.dto.TeamResponse;
import com.founderlink.team.entity.TeamMember;
import com.founderlink.team.event.EventPublisher;
import com.founderlink.team.exception.InvalidInputException;
import com.founderlink.team.exception.ResourceNotFoundException;
import com.founderlink.team.repository.TeamRepository;
import com.founderlink.team.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private EventPublisher eventPublisher;
    @InjectMocks private TeamService teamService;

    private TeamMember member;
    private TeamRequest request;

    @BeforeEach
    void setUp() {
        member = new TeamMember();
        member.setId(1L);
        member.setStartupId(1L);
        member.setUserId(2L);
        member.setRole("CTO");
        member.setStatus("PENDING");

        request = new TeamRequest();
        request.setStartupId(1L);
        request.setUserId(2L);
        request.setRole("CTO");
    }

    @Test
    void inviteMember_success() {
        when(teamRepository.save(any())).thenReturn(member);
        doNothing().when(eventPublisher)
                .publishTeamInviteSent(any(), any(), any(), any());

        TeamResponse response = teamService.inviteMember(request);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        assertEquals("CTO", response.getRole());
        verify(eventPublisher, times(1))
                .publishTeamInviteSent(any(), any(), any(), any());
    }

    @Test
    void inviteMember_nullStartupId_throwsInvalidInputException() {
        request.setStartupId(null);
        assertThrows(InvalidInputException.class,
                () -> teamService.inviteMember(request));
    }

    @Test
    void inviteMember_nullUserId_throwsInvalidInputException() {
        request.setUserId(null);
        assertThrows(InvalidInputException.class,
                () -> teamService.inviteMember(request));
    }

    @Test
    void acceptInvitation_success() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(member));
        when(teamRepository.save(any())).thenReturn(member);

        TeamResponse response = teamService.acceptInvitation(1L);

        assertEquals("ACCEPTED", response.getStatus());
    }

    @Test
    void acceptInvitation_notFound_throwsResourceNotFoundException() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> teamService.acceptInvitation(99L));
    }

    @Test
    void getTeamByStartup_returnsList() {
        when(teamRepository.findByStartupId(1L)).thenReturn(List.of(member));
        List<TeamResponse> result = teamService.getTeamByStartup(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStartupId());
    }
}
