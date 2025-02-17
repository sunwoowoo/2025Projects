package org.zerock.projects.service.machines;

import org.zerock.projects.domain.ProductionOrder;
import org.zerock.projects.domain.machines.Process;

public interface IProcessService {
    void saveProcess(Process process);
}
