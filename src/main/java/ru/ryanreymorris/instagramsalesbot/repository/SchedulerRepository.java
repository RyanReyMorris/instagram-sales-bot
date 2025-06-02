package ru.ryanreymorris.instagramsalesbot.repository;


import ru.ryanreymorris.instagramsalesbot.entity.SchedulerJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerJobInfo, String> {

    Collection<SchedulerJobInfo> findAllByJobGroup(String jobGroup);
}