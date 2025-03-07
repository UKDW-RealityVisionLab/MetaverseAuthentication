package org.ukdw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Creator: dendy
 * Date: 7/11/2024
 * Time: 12:52 PM
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppsCheckPermissionResponse {
    private boolean status;
}
