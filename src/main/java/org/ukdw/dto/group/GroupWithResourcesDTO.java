package org.ukdw.dto.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.ukdw.entity.GroupEntity;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class GroupWithResourcesDTO {
    private Long id;
    private String groupName;
    private long permission;
    private Map<Long, String> resources;

    public GroupWithResourcesDTO(GroupEntity group, Map<Long, String> resources) {
        this.id = group.getId();
        this.groupName = group.getGroupname();
        this.permission = group.getPermission();
        this.resources = resources;
    }
}
