package org.Roclh.data.model.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.repositories.ManagerRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerService {
    private final ManagerRepository managerRepository;

    public boolean addManager(@NonNull ManagerModel managerModel){
        try{
            managerRepository.save(managerModel);
            return true;
        }catch (Exception e){
            log.error("Failed to add manager", e);
            return false;
        }
    }

    public boolean delManager(@NonNull String telegramId){
        return managerRepository.deleteByTelegramId(telegramId) > 0;
    }
}
