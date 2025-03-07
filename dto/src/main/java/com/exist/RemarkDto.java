package com.exist;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemarkDto {
    private Long id;
    private String message;  // The actual remark content
    private LocalDateTime createdDate;  // Timestamp when the remark was added
    private String createdBy;  // User who added the remark
}
