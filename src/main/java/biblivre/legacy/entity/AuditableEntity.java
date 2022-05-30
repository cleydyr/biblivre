package biblivre.legacy.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@MappedSuperclass
public class AuditableEntity {
    @Column(nullable = false)
    private LocalDateTime created;

    @Column private Integer createdBy;

    @Column(nullable = false)
    private LocalDateTime modified;

    @Column private Integer modifiedBy;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
