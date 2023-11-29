package bamboo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "people")
public class PeopleEntity {
    @Id
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "status", nullable = false)
    private int status;
}
