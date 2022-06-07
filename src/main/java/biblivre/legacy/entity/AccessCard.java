package biblivre.legacy.entity;

import biblivre.administration.accesscards.AccessCardStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "access_cards", schema = "single")
public class AccessCard extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column private String code;

    @Column(name = "status")
    private String rawAccessCardStatus;

    @Transient private AccessCardStatus accessCardStatus;

    @PostLoad
    private void loadAccessCardStatus() {
        this.accessCardStatus = AccessCardStatus.fromString(rawAccessCardStatus).get();
    }

    @PrePersist
    private void serializeTransient() {
        this.rawAccessCardStatus = accessCardStatus.toString();
    }

    public int getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public AccessCardStatus getAccessCardStatus() {
        return this.accessCardStatus;
    }

    public void setAcessCardStatus(AccessCardStatus accessCardStatus) {
        this.accessCardStatus = accessCardStatus;

        this.rawAccessCardStatus = accessCardStatus.name();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
