package com.uguimar.authms.infrastructure.output.persistence.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("modules")
public class ModuleEntity {

    @Id
    private String id;
    @Column("name")
    private String name;
    @Column("description")
    private String description;
    private boolean enabled;


}
