package com.founderlink.team.controller;

import com.founderlink.team.dto.TeamRequest;
import com.founderlink.team.dto.TeamResponse;
import com.founderlink.team.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // Invite co-founder
    @PostMapping("/invite")
    public ResponseEntity<TeamResponse> inviteMember(
            @RequestBody TeamRequest request) {
        return ResponseEntity.ok(teamService.inviteMember(request));
    }

    // Accept invitation
    @PutMapping("/accept/{id}")
    public ResponseEntity<TeamResponse> acceptInvitation(
            @PathVariable Long id) {
        return ResponseEntity.ok(teamService.acceptInvitation(id));
    }

    // Get team by startup
    @GetMapping("/startup/{startupId}")
    public ResponseEntity<List<TeamResponse>> getTeamByStartup(
            @PathVariable Long startupId) {
        return ResponseEntity.ok(teamService.getTeamByStartup(startupId));
    }

    // Get teams by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamResponse>> getTeamByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(teamService.getTeamByUser(userId));
    }

    // Get all members
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllMembers() {
        return ResponseEntity.ok(teamService.getAllMembers());
    }
}