package ch.martinelli.demo.data.service;

import ch.martinelli.demo.data.entity.SamplePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, UUID>, JpaSpecificationExecutor<SamplePerson> {

}
