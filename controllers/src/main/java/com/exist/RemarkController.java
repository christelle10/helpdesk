package com.exist;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/remarks") //(Clearly scoped under a specific ticket)
@RequiredArgsConstructor
public class RemarkController {

    private final RemarkService remarkService;
    private final RemarkRepository remarkRepository;

    @PostMapping
    public ResponseEntity<RemarkDto> addRemark(@PathVariable Long ticketId, @RequestBody RemarkDto remarkDto) {
        RemarkDto createdRemark = remarkService.addRemark(ticketId, remarkDto);
        return ResponseEntity.ok(createdRemark);
    }

    @DeleteMapping("/{remarkId}")
    public ResponseEntity<Void> deleteRemark(@PathVariable Long ticketId, @PathVariable Long remarkId) {
        remarkService.deleteRemark(ticketId, remarkId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<RemarkDto>> getRemarksByTicketId(@PathVariable Long ticketId) {
        List<RemarkDto> remarks = remarkService.getRemarksByTicketId(ticketId);
        return ResponseEntity.ok(remarks);
    }
}
